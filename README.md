# JSAutoCrud — IntelliJ IDEA plugin

JSAutoCrud speeds up Spring Boot development by generating complete REST CRUD layers
(entity, repository, service and controller) powered by the
[generic-auto-crud](https://github.com/ISYS-Dcore/generic-auto-crud) library.

## Installation

1. Download the latest plugin `zip` from the
   [releases page](https://github.com/Domingos-Masta/JSAutoCrudPlugin/releases).
2. Open IntelliJ IDEA → **Settings / Preferences** → **Plugins**.
3. Click the gear icon → **Install Plugin from Disk...** and select the downloaded zip.
4. Restart the IDE.

## Pre-requisites

Your Spring Boot 3.x project must declare the library dependency in `pom.xml`:

```xml
<dependency>
    <groupId>io.github.isys-dcore</groupId>
    <artifactId>generic-auto-crud</artifactId>
    <version>0.7.5</version>
</dependency>
```

The plugin validates this dependency before opening the wizard, and warns you if the
installed version is older than the one the generated code targets.

## Usage

Right-click the project root (or any file) in the **Project** tool window and choose
**Generate JSA Classes**.

The wizard walks through four steps:

1. **Creation mode** — generate everything from scratch, from an existing entity, or only
   a single layer (repository / service / controller).
2. **Entity details** —
   - *From scratch:* entity names (comma separated), database type (SQL or MongoDB) and ID type.
   - *From an existing entity:* pick any class that extends `GenericSqlEntity` or `GenericNoSqlEntity`.
3. **Associations** — (SQL only, when generating more than one entity) define JPA relationships.
4. **Options** — toggle Controller / Service / DTO / Test generation per entity, then click **Generate**.

### What gets generated

For a `Person` entity with SQL and ID type `UUID`, the plugin generates:

| Class | Extends / Implements |
|---|---|
| `Person` | `GenericSqlEntity<UUID>` |
| `PersonRepository` | `GenericSqlRepository<Person, UUID>` |
| `PersonServiceImpl` | `GenericSqlRestServiceAbstract<Person, PersonRepository, UUID>` |
| `PersonRestController` | `GenericRestControllerAbstract<Person, PersonServiceImpl, UUID>` |
| `PersonDto` | `GenericDto<UUID>` |
| `PersonDtoMapper` | `GenericDTOMapper<PersonDto, Person>` |
| `PersonServiceImplDto` | `GenericSqlRestServiceAbstractDto<...>` |
| `PersonRestControllerDto` | `GenericRestControllerAbstractDto<...>` |
| `PersonControllerTest` | `GenericRestIntegrationTestsImpl<Person, UUID>` |

MongoDB entities extend `GenericNoSqlEntity` and use the NoSQL repository/service
variants (`GenericNoSqlRepository`, `GenericNoSqlRestServiceAbstract`).

Generated controllers are mapped under `/api/{entity}`.

## Building from source

```sh
./gradlew buildPlugin
```

The built zip is available under `build/distributions`.

## Contributing

Pull requests are welcome. For major changes, please open an issue first to discuss
what you would like to change.

## License

[MPL-2.0](https://choosealicense.com/licenses/mpl-2.0/)
