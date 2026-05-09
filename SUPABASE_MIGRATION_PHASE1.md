# VidyaSetu — Supabase Schema Audit & Migration Guide
### Phase 1 Release (UI-Complete Features Only)

> **Scope of this document:** Phase 1 ships **only** the screens whose UI is already
> built. Every back-end requirement below is derived from those screens — nothing
> speculative. Database changes that exist purely for not-yet-built screens are
> intentionally **deferred** to Phase 2+ migrations.
>
> **Audit target:** `supabase schema` (v2.1) at the repo root.

---

## 1. Phase 1 UI Surface (what actually ships)

| # | Screen / Component | File | Data it consumes / produces |
|---|---|---|---|
| 1 | **Common Landing** (hero, featured schools, social proof, entry-points, moat showcase, portal access, final CTA, footer) | `presentation/landing/CommonLandingScreen.kt` | `List<School>` (id, name, location, board, description, imageUrl) |
| 2 | **Auth Bottom Sheet** (Entry → OTP, role: ADMIN / PARENT, contact = email or phone) | `ui/auth/AuthBottomSheet.kt` | Supabase Auth (OTP) → row in `public.users` |
| 3 | **Drawer / TopBar / SearchBar** (theme switcher, navigation chrome, board / location / query) | `ui/components/EduTrustDrawer.kt`, `EduTrustTopBar.kt`, `EduTrustSearchBar.kt` | Local theme pref (DataStore today), display-only nav |
| 4 | **Skeleton / Shimmer / Error states** | `ui/components/LandingSkeleton.kt`, `Shimmer.kt` | None (UI-only) |

Everything else listed in `supabase schema v2.1` (students, daily_progress,
academic_records, achievements, ai_reports, fee_structures, fee_payments,
student_parent_link) supports **Phase 2+** screens that have **no UI yet** and
is therefore out of scope for this Phase 1 release. Those tables stay in the
schema (so the rollout is non-destructive), but none of their columns gate
Phase 1.

---

## 2. Mapping: Screens → Tables / Columns

### 2.1 Landing Screen
The landing page reads a list of public schools. The Kotlin model is:

```kotlin
data class School(
    val id: String,
    val name: String,
    val location: String,
    val board: String,
    val description: String,
    val imageUrl: String
)
```

This maps to **`school_directory`** (the public-readable table; `schools` is
the private operational record and is not safe to expose unauthenticated):

| Kotlin field   | Source column in `school_directory`                                      | Notes |
|----------------|---------------------------------------------------------------------------|-------|
| `id`           | `id` (UUID, cast to TEXT)                                                 | OK |
| `name`         | `name`                                                                    | OK |
| `location`     | `city` (preferred) — fallback to `locality`, `district`                   | Composed in the `school_card_v1` view (see §3.1) |
| `board`        | `board::TEXT` (enum cast)                                                 | OK |
| `description`  | **MISSING** — there is no `description` / `tagline` column in v2.1       | See **MIGRATION 1** |
| `imageUrl`     | `cover_photo_url` — fallback to `logo_url`                                | OK; composed in view |

✅ **RLS already correct:** policy `"public reads active school_directory"`
allows unauthenticated `SELECT` where `is_active = TRUE`. This is what the
landing page relies on (anonymous user → can see schools).

### 2.2 Auth Bottom Sheet
The sheet collects **email or phone** and a **role** (ADMIN / PARENT). The
Kotlin enum `AuthRole { ADMIN, PARENT }` maps to the `user_role` enum values:

| AuthRole (Kotlin) | user_role (Postgres) | Where it lands |
|---|---|---|
| `ADMIN`           | `school_admin`       | `public.users.role` |
| `PARENT`          | `parent`             | `public.users.role` |

**Critical gap:** the existing trigger `handle_new_auth_user()` always sets
`role = 'parent'` and ignores any role hint passed at sign-up. If an admin
signs up via the same OTP flow, their `users.role` will be wrong on row 0
and someone has to flip it manually. We fix this by reading
`raw_user_meta_data->>'role'` (set client-side via Supabase Auth's
`signInWithOtp({ data: { role: 'school_admin' | 'parent' } })`).
See **MIGRATION 2**.

✅ **All other Auth columns already exist** on `public.users`:
`id` (FK to `auth.users`), `full_name`, `phone`, `email`, `language_pref`,
`is_active`, `last_login_at`, `created_at`, `updated_at`. No further DDL.

### 2.3 Drawer — Theme Switcher
The Drawer's three-way theme toggle (LIGHT / DARK / MIDNIGHT) is currently
persisted **locally** via `PreferenceRepository` (DataStore) and read by
`MainViewModel.themeName`. This is **device-local only**.

Decision for Phase 1: **Do NOT add a `theme_pref` column yet.** Cross-device
sync is a Phase 2 nicety, the screen works perfectly with local storage, and
adding it now would force us to wire repository writes that aren't in the UI
yet. A pre-baked migration is included as **MIGRATION 3 (OPTIONAL)** so it
can be applied in one click later without rework.

### 2.4 Drawer — Language Switcher (forward-compat)
Drawer currently shows static labels. The `users.language_pref` column
(enum `'en'|'hi'|'ur'`) already exists, so when the language toggle UI lands
in Phase 2 no DDL is required. ✅ **No action.**

---

## 3. Required Migrations for Phase 1

There are exactly **two required** migrations and one **optional** one.

### MIGRATION 1 (REQUIRED) — Landing description + safe public view

The landing screen needs a one-line description per school. Add a
`tagline` column (short, indexable, plain text — not the giant `full_address`)
and expose a tightly-scoped **read view** that is the only thing the public
client touches. The view also pre-composes `location` and `image_url` so the
Kotlin client never has to do null-coalescing in SQL.

```sql
-- =============================================================================
-- MIGRATION 1 — landing-page tagline + public read view
-- Safe to run multiple times (uses IF NOT EXISTS / CREATE OR REPLACE).
-- =============================================================================

-- 1.1 New column on school_directory
ALTER TABLE school_directory
  ADD COLUMN IF NOT EXISTS tagline TEXT;

COMMENT ON COLUMN school_directory.tagline IS
  'Short marketing line shown on landing-page school cards (max ~140 chars).';

-- 1.2 Length guard (keeps cards visually consistent)
ALTER TABLE school_directory
  DROP CONSTRAINT IF EXISTS chk_sd_tagline_len;

ALTER TABLE school_directory
  ADD CONSTRAINT chk_sd_tagline_len
  CHECK (tagline IS NULL OR char_length(tagline) <= 200);

-- 1.3 Public, denormalised read view used by the Kotlin client
CREATE OR REPLACE VIEW school_card_v1 AS
SELECT
  sd.id::TEXT                                                    AS id,
  sd.name                                                        AS name,
  COALESCE(NULLIF(sd.city, ''),
           NULLIF(sd.locality, ''),
           sd.district)                                          AS location,
  sd.board::TEXT                                                 AS board,
  COALESCE(sd.tagline, '')                                       AS description,
  COALESCE(NULLIF(sd.cover_photo_url, ''), sd.logo_url, '')      AS image_url,
  sd.sri_score                                                   AS sri_score,
  sd.is_accepting_admissions                                     AS is_accepting_admissions,
  sd.spotlight_rank                                              AS spotlight_rank
FROM school_directory sd
WHERE sd.is_active = TRUE;

COMMENT ON VIEW school_card_v1 IS
  'Phase 1 landing-page projection of school_directory. Public-readable.';

-- 1.4 Make the view public-readable (it inherits the underlying RLS, but we
--     also need explicit grants for PostgREST/anon to expose it on REST/RPC).
GRANT SELECT ON school_card_v1 TO anon, authenticated;

-- 1.5 Optional: index to power "spotlight" ordering used on landing page
CREATE INDEX IF NOT EXISTS idx_sd_spotlight
  ON school_directory (spotlight_rank ASC NULLS LAST, sri_score DESC)
  WHERE is_active = TRUE;
```

**Kotlin client change required (already compatible — no code change needed today):**
the Ktor / Supabase data source should target `school_card_v1` instead of
`school_directory` once it is wired to real data. The mock `KtorSchoolApi`
returning hard-coded rows is unaffected. When swapping in
`postgrest.from("school_card_v1").select()`, the column names already line up
1:1 with the `School` data class (`id`, `name`, `location`, `board`,
`description`, `image_url`), so the only code touch is one string literal.

---

### MIGRATION 2 (REQUIRED) — Honour signup-time role + sane defaults

The current `handle_new_auth_user()` trigger ignores `role`/`phone`/
`language_pref` from `raw_user_meta_data`. Phase 1 Auth UI lets a user pick
ADMIN vs PARENT before OTP; that choice **must** survive into the `users`
row, otherwise every admin signup is silently downgraded to parent.

```sql
-- =============================================================================
-- MIGRATION 2 — richer handle_new_auth_user() trigger
-- Idempotent: CREATE OR REPLACE only. Existing rows are NOT touched.
-- =============================================================================

CREATE OR REPLACE FUNCTION handle_new_auth_user()
RETURNS TRIGGER
LANGUAGE plpgsql
SECURITY DEFINER
SET search_path = public
AS $$
DECLARE
  v_role     user_role;
  v_lang     lang_pref;
  v_phone    TEXT;
  v_full     TEXT;
BEGIN
  -- Role: pick from metadata if it's a recognised value, else 'parent'.
  BEGIN
    v_role := COALESCE(
      NULLIF(NEW.raw_user_meta_data->>'role', '')::user_role,
      'parent'::user_role
    );
  EXCEPTION WHEN invalid_text_representation THEN
    v_role := 'parent'::user_role;
  END;

  -- Language: hi default, but accept en/ur from client.
  BEGIN
    v_lang := COALESCE(
      NULLIF(NEW.raw_user_meta_data->>'language_pref', '')::lang_pref,
      'hi'::lang_pref
    );
  EXCEPTION WHEN invalid_text_representation THEN
    v_lang := 'hi'::lang_pref;
  END;

  -- Phone: prefer auth.users.phone, else metadata.
  v_phone := COALESCE(NEW.phone, NULLIF(NEW.raw_user_meta_data->>'phone', ''));

  -- Full name: metadata first, then sensible fallback.
  v_full := COALESCE(
    NULLIF(NEW.raw_user_meta_data->>'full_name', ''),
    NULLIF(split_part(NEW.email, '@', 1), ''),
    'New User'
  );

  INSERT INTO public.users (
    id, email, phone, full_name, role, language_pref, created_at
  )
  VALUES (
    NEW.id, NEW.email, v_phone, v_full, v_role, v_lang, NOW()
  )
  ON CONFLICT (id) DO NOTHING;

  RETURN NEW;
END;
$$;

-- Trigger declaration is unchanged but re-asserted for safety:
DROP TRIGGER IF EXISTS trg_on_auth_user_created ON auth.users;
CREATE TRIGGER trg_on_auth_user_created
  AFTER INSERT ON auth.users
  FOR EACH ROW EXECUTE FUNCTION handle_new_auth_user();
```

**Client-side contract (already matches the Auth UI):**
when calling `supabase.auth.signInWithOtp(...)` from the bottom sheet, pass:

```kotlin
// Kotlin (supabase-kt) — pseudocode for when AuthBottomSheet wires real auth
auth.signInWith(OTP) {
    email = contactInfo            // or phone, depending on input
    data = buildJsonObject {
        put("role", when (role) {
            AuthRole.ADMIN  -> "school_admin"
            AuthRole.PARENT -> "parent"
        })
        put("language_pref", "hi") // Drawer default for Phase 1
    }
}
```

The current `AuthBottomSheet.kt` already exposes `role: AuthRole`, so this
is a one-line wiring change at the moment Supabase Auth replaces the
placeholder OTP handler.

---

### MIGRATION 3 (OPTIONAL — defer to Phase 2)

Cross-device theme sync. **Do not run during Phase 1** — included so the
column / RLS / trigger plumbing can land in one click later.

```sql
-- =============================================================================
-- MIGRATION 3 (OPTIONAL, Phase 2) — cross-device theme preference
-- =============================================================================

-- 3.1 Enum
DO $$ BEGIN
  CREATE TYPE theme_pref AS ENUM ('light', 'dark', 'midnight', 'system');
EXCEPTION
  WHEN duplicate_object THEN NULL;
END $$;

-- 3.2 Column
ALTER TABLE users
  ADD COLUMN IF NOT EXISTS theme_pref theme_pref DEFAULT 'system';

-- 3.3 RLS — already covered by "users update themselves" on UPDATE,
--     and "users see themselves" on SELECT. No new policy needed.
```

When this is enabled, `MainViewModel.themeName` should write through to
`public.users.theme_pref` in addition to the local DataStore (last-write-wins
on app launch).

---

## 4. RLS Verification Checklist (Phase 1)

| Action                                                | Required policy                              | Status in v2.1 |
|-------------------------------------------------------|----------------------------------------------|----------------|
| Anonymous user views landing schools                  | `public reads active school_directory`       | ✅ Present |
| Anonymous user views `school_card_v1`                 | Inherits from `school_directory` + GRANT     | ✅ After Migration 1 |
| Newly signed-up user reads own `users` row            | `users see themselves`                       | ✅ Present |
| Newly signed-up user updates own `users` row          | `users update themselves`                    | ✅ Present |
| Trigger writes to `public.users` from `auth.users`    | `SECURITY DEFINER` on `handle_new_auth_user` | ✅ Present (improved by Migration 2) |
| Super admin can curate `school_directory`             | `super_admin manages school_directory`       | ✅ Present |

No additional RLS work is required for Phase 1.

---

## 5. Apply Order

Run in Supabase → SQL Editor in this exact order:

1. The full v2.1 schema file (`supabase schema`) — only if the project has not been initialised yet.
2. **MIGRATION 1** (required for landing).
3. **MIGRATION 2** (required for auth role correctness).
4. _Skip_ **MIGRATION 3** until Phase 2 ships the cross-device theme sync.

Each migration is idempotent (`IF NOT EXISTS` / `CREATE OR REPLACE` /
`DROP CONSTRAINT IF EXISTS`) and can be re-run safely.

---

## 6. Out-of-Scope (NOT migrated in Phase 1)

These belong to Phase 2+ screens that are not yet built. Their tables exist
in `supabase schema` v2.1, but **no Phase 1 client code reads or writes them**,
so they remain dormant:

- `students`, `student_parent_link`
- `daily_progress`
- `academic_records`
- `achievements`
- `ai_reports` (PEWS, term reports, scholarship matching)
- `fee_structures`, `fee_payments`

When the corresponding screens land, each will get its own
`SUPABASE_MIGRATION_PHASE<N>.md` next to this one — same format, same
idempotent style, same RLS-first checklist.

---

## 7. TL;DR

* Existing schema v2.1 covers ~95% of Phase 1 already. ✅
* **Two required migrations:**
  1. `school_directory.tagline` + `school_card_v1` view (landing).
  2. Smarter `handle_new_auth_user()` (auth role + lang from metadata).
* One optional migration (cross-device theme) deferred to Phase 2.
* RLS already correctly permits anonymous landing reads and authenticated self-reads.
* No data-loss / no destructive change. Re-runnable.
