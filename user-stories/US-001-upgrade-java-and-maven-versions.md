# US-001: Upgrade Java and Maven Versions Across All Repositories

**Issue:** [#1 — Upgrade Java and Maven Versions Across All Repositories](https://github.com/lofidewanto/maven-course-examples/issues/1)  
**Status:** Open  
**Priority:** High  
**Created:** 2026-03-23

---

## User Story

**As a** software developer / DevOps engineer  
**I want to** update the Java version to 21 and Maven version to 3.9.14 across all modules in this repository  
**So that** we ensure consistency, improve security, leverage new language features, and maintain compatibility with supported toolchains

---

## Context and Background

The repository `maven-course-examples` is a multi-module Maven project used for training purposes. It currently has three top-level aggregator modules: `examples/`, `solutions/`, and `exercises/`.

### Current State (as of 2026-03-23)

| Aspect | Current State |
|---|---|
| Stated target Java version (README.md) | Java 11 |
| Actual Java versions in pom.xml files | Mixed: Java 21 (some), Java 11 (majority of active modules), Java 1.8 (several legacy examples), Java 1.7 (deprecated/commented-out) |
| Stated target Maven version (README.md) | Maven 3.9.6 |
| Maven prerequisites (root pom.xml) | `[3.0.5,)` (any 3.0.5+) |
| CI/CD (.travis.yml) | `oraclejdk8` — severely outdated |
| Maven Wrapper | **Not present** — no `mvnw` or `.mvn/wrapper/` |

### Target State

| Aspect | Target |
|---|---|
| Java version (all active modules) | **Java 21** |
| Maven version | **Maven 3.9.14** |
| Maven Wrapper | Present and configured for 3.9.14 |
| CI/CD | Updated to Java 21 |
| README.md | Reflects Java 21 / Maven 3.9.14 |

---

## Scope

### In Scope

All `pom.xml` files across the repository, grouped by category:

#### Group A — Active modules using Java 11 (straightforward upgrade to 21)
These modules already target Java 11 and should upgrade cleanly to Java 21:

| Module Path | Notes |
|---|---|
| `examples/proficio/` | `maven.compiler.source/target=11`, `java.version=11` |
| `examples/maven-source-plugin/` | Java 11 |
| `examples/jpa-filtering/` | Java 11 |
| `examples/jarsigner-example/` | Java 11 |
| `examples/git-scm-example/` | Java 11 |
| `examples/dependency-version-ranges/` | Java 11 |
| `examples/maven-uppercase-plugin-usage/` | Java 11 |
| `exercises/jpa-filtering-exercise/` | Java 11 |
| `exercises/mvn.05.exercise-multi/simple-weather/` | Java 11 |
| `exercises/mvn.05.exercise-multi/simple-webapp/` | Java 11 |
| `solutions/mvn.02.solution/` | Java 11 |
| `solutions/mvn.03.solution-profile/` | Java 11 |
| `solutions/mvn.03.solution-migration/` | Java 11 |
| `solutions/mvn.04.solution-webapp/` | Java 11 |
| `solutions/mvn.05.solution-multi/` (simple-webapp, simple-weather) | Java 11 |

#### Group B — Active modules using Java 1.8 (upgrade to 21, compatibility check required)
These modules specify Java 1.8 and need compatibility review before upgrading:

| Module Path | Notes |
|---|---|
| `examples/ejbs/` (`application/`, `sessionbeans/`, `webclient/`) | `java.version=1.8`; EJB project — Java EE API compatibility must be checked |
| `examples/ejb-jboss-simple/` | `java.version=1.8`, `maven.compiler.source/target=1.8` |
| `examples/antrun-example/` | `maven.compiler.source/target=1.8` |
| `examples/distribution-example/` | `maven.compiler.source/target=1.8` |
| `examples/deployment-assembly-example/` | `maven.compiler.source/target=1.8` |

#### Group C — Modules with old/variable version refs (audit and update)

| Module Path | Current Version Setting | Notes |
|---|---|---|
| `examples/maven-parent/` | `java.version=1.7`; Maven prereq `3.0.5` | Parent POM — affects all inheriting children |
| `examples/reports/` | `java.version=1.7`; Maven prereq `3.0.5` | |
| `solutions/mvn.03.solution-assembly/` | `${jdk.version}` property | Must set `jdk.version=21` |
| `examples/project-pattern-archetype/` | `${javaVersion}` in archetype template | Archetype template needs updated defaults |

#### Group D — No Java version set (inherit or set explicitly)

| Module Path | Notes |
|---|---|
| `examples/spring-bom/` | BOM POM — no compiler settings needed, but review dependencies |
| `examples/maven-uppercase-plugin/` | Custom Maven plugin; `maven.version=3.0.5` property needs update |
| `examples/local-scm/` | |
| `examples/simple-release/` | |
| `examples/maven-profiles/` | |
| `examples/maven-touch-plugin/` | Custom plugin |

#### Group E — CI/CD and Build Infrastructure

| File | Change Required |
|---|---|
| `.travis.yml` | Replace `oraclejdk8` with `openjdk21` |
| `.mvn/extensions.xml` | Verify extension versions remain compatible with Maven 3.9.14 |
| Root `pom.xml` `<prerequisites>` | Update minimum Maven version from `[3.0.5,)` to `[3.9,)` or pin `3.9.14` |
| Maven Wrapper | **Add** `.mvn/wrapper/maven-wrapper.properties` pinned to `3.9.14` |
| `README.md` | Update documented versions from `Maven 3.9.6 / Java 11` to `Maven 3.9.14 / Java 21` |

### Out of Scope

- The commented-out/disabled modules (`jaxb-code-generation`, `jaxws-*`, `jaxws-fraction-*`) — these are already excluded from the default build. They may be re-evaluated in a separate issue since they are noted as broken on Java 11+.
- Functional refactoring or feature additions beyond what is necessary for Java 21 compatibility.
- Migrating away from Travis CI to another CI provider (a separate concern).

---

## Acceptance Criteria

1. **Version Upgrade**
   - [ ] All active modules compile with `source` and `target` (or `release`) set to `21`
   - [ ] Maven Wrapper is configured for Maven `3.9.14`

2. **Build Configuration Updated**
   - [ ] Root `pom.xml` `<prerequisites>` updated to `[3.9,)`
   - [ ] `maven-compiler-plugin` version is `3.13.0` or newer in all modules (to ensure Java 21 support)
   - [ ] `examples/maven-parent/pom.xml` (the teaching parent) updated to Java 21 and current plugin versions
   - [ ] Maven Wrapper (`mvnw` / `.mvn/wrapper/maven-wrapper.properties`) added, pointing to 3.9.14
   - [ ] `.travis.yml` updated from `oraclejdk8` to `openjdk21`

3. **Compatibility Verified**
   - [ ] `mvn clean verify` passes at root level (default profile)
   - [ ] `mvn clean verify -P exercises` passes
   - [ ] No use of APIs removed in Java 9–21 (e.g., `sun.*` internals, deprecated `javax.*` in non-EE contexts)
   - [ ] EJB modules (`ejbs/`, `ejb-jboss-simple/`) verified to compile — or explicitly documented as requiring a separate upgrade track

4. **Plugin Version Alignment**
   - [ ] `maven-compiler-plugin` upgraded to `3.13.0` across all modules where pinned
   - [ ] `maven-surefire-plugin` is `3.x` to support Java 21 test execution
   - [ ] `opentelemetry-maven-extension` (`1.33.0-alpha` in `.mvn/extensions.xml`) version verified for Maven 3.9.14 compatibility

5. **Documentation Updated**
   - [ ] `README.md` states `Maven: 3.9.14` and `Java: 21`
   - [ ] Note about JAXB/JAXWS modules updated to reflect current status

6. **Definition of Done**
   - [ ] All active modules build green with Java 21 and Maven 3.9.14
   - [ ] All tests pass
   - [ ] CI is green (or `.travis.yml` is updated and validated)
   - [ ] No unresolved deprecation warnings related to the version upgrade

---

## Technical Notes and Risk Assessment

### Risk: EJB Modules (Medium)
`examples/ejbs/` and `examples/ejb-jboss-simple/` use Java EE APIs. Java EE was moved to Jakarta EE starting with Java 9. On Java 21, `javax.*` packages may be unavailable depending on the dependency declarations. Recommendation: audit imports and dependency declarations before upgrading; if JBoss/WildFly-specific runtime APIs are involved, they may need Jakarta EE equivalents.

### Risk: `maven-parent` POM acts as a teaching example (Low-Medium)
`examples/maven-parent/pom.xml` is intentionally a teaching artifact demonstrating Maven parent POM patterns. It currently specifies Java 1.7 and very old plugin versions. Upgrading it changes the instructional content. The upgrade should be reflected in the accompanying course material or exercise instructions.

### Risk: `maven-uppercase-plugin` and `maven-touch-plugin` (Low)
These are custom Maven plugins. They reference Maven API classes. Maven 3.9.x plugin API is largely backward compatible, but `maven.version=3.0.5` in the plugin POM is misleading and should be updated. The actual runtime compatibility must be verified by building and running the plugin.

### Risk: Flex multi-project profile (`-P flex`) (Low)
`examples/flex-multiproject/` is gated behind the `flex` profile and targets Flex (Adobe/Apache Flex). Flex tooling is largely abandoned. This module should be assessed — it may need to remain on a Java 8 fork or be documented as legacy/unsupported.

### Risk: `maven-enforcer-plugin` rules (Low)
`examples/maven-parent/pom.xml` has a `maven-enforcer-plugin` rule requiring `[3.0.5,)`. This must be updated to `[3.9,)` to align with the new minimum.

### Compatibility Notes for Java 21
- Java 21 introduces **virtual threads** and **record patterns** (finalized). No source changes are needed for existing code unless it uses removed APIs.
- The `--release 21` flag for `maven-compiler-plugin` is preferred over separate `source`/`target` to also enforce API compatibility.
- Some older tests relying on reflection may need `--add-opens` JVM arguments passed via `maven-surefire-plugin` configuration.

---

## Task Breakdown / Sub-Tasks

| # | Task | Scope | Effort |
|---|---|---|---|
| T-01 | Add Maven Wrapper pinned to 3.9.14 | `.mvn/wrapper/maven-wrapper.properties`, `mvnw`, `mvnw.cmd` | Small |
| T-02 | Update root `pom.xml` prerequisites and metadata | `pom.xml` | Small |
| T-03 | Upgrade Group A modules to Java 21 (Java 11 → 21) | 15 modules | Medium |
| T-04 | Upgrade `maven-compiler-plugin` to 3.13.0 across all modules | All pinned modules | Medium |
| T-05 | Upgrade `maven-surefire-plugin` to 3.x across all modules | All pinned modules | Small |
| T-06 | Audit and upgrade Group B modules (Java 1.8 → 21) | 5 modules (EJB-heavy) | Medium-High |
| T-07 | Audit and upgrade Group C modules (variable/old versions) | 4 modules | Medium |
| T-08 | Update `examples/maven-parent/pom.xml` teaching POM | 1 file | Small |
| T-09 | Update `.travis.yml` to Java 21 (`openjdk21`) | `.travis.yml` | Small |
| T-10 | Verify `.mvn/extensions.xml` extension compatibility | `.mvn/extensions.xml` | Small |
| T-11 | Run full build and fix any compilation/test failures | Full repo | Medium-High |
| T-12 | Update `README.md` with new versions | `README.md` | Small |
| T-13 | Assess Flex multiproject module (`-P flex`) and document status | `examples/flex-multiproject/` | Small |

**Total estimated effort:** Medium-Large (2–4 days for a developer familiar with Maven multi-module projects)

---

## Definition of Done

- [ ] All tasks T-01 through T-12 completed
- [ ] `mvn clean verify` (default profile) exits with BUILD SUCCESS
- [ ] `mvn clean verify -P exercises` exits with BUILD SUCCESS
- [ ] `.travis.yml` pipeline passes on Java 21
- [ ] `README.md` updated
- [ ] PR reviewed and merged
