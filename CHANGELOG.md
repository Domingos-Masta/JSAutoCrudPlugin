# Changelog

All notable changes to the JSAutoCrud plugin are documented in this file.

## [0.3.0] - 2026-10-28

### Aligned with generic-auto-crud 0.7.x

- Generated classes now target the latest `generic-auto-crud` API:
  - SQL: `GenericSqlEntity`, `GenericSqlRepository`, `GenericSqlRestServiceAbstract`
  - MongoDB: `GenericNoSqlEntity`, `GenericNoSqlRepository`, `GenericNoSqlRestServiceAbstract`
  - DTO: `GenericDto`, `GenericDTOMapper`, `GenericSqlRestServiceAbstractDto`,
    `GenericNoSqlRestServiceAbstractDto`, `GenericRestControllerAbstractDto`
  - Tests: `GenericRestIntegrationTestsImpl`
- Controllers are mapped under `/api/{entity}`.

### New features

- Database type selection (SQL vs MongoDB) with automatic ID-type resolution.
- Optional DTO and integration-test generation per entity.
- Creation modes selectable directly from the project context menu:
  - From Scratch, From Existing Entity, Repositories Only, Services Only, Controllers Only.
- Dependency version validation now resolves Maven `${...}` properties and
  `dependencyManagement`, so property-based versions no longer trigger false warnings.

### UI improvements

- Redesigned the wizard: entity details → associations → options.
- Modernized the association dialog.
- Added icons to the context-menu submenu items.

### Fixes

- Fixed association generation NPE and options-table sync.
- Fixed package detection (no longer uses `groupId` as the package name).
- Fixed `ClassExtendsFinder` for entities without an extends list.

### Compatibility

- Extended IDE support up to build `262.*`.

## [0.2.0] - 2026-10-28

- Modernized UI and aligned generated code with the latest library API.

## [0.1.0] - 2024-10-10

- Initial public version.

## [0.0.5] - 2024-09-19

- First release.
