Project brief (for Cursor)

Goal: Modernize CFLint to fully support Adobe CF 2023/2025 and Lucee 5.4/6.x, add dialect/version awareness, and implement tolerant parsing so unknown syntax doesn’t abort a file.
Approach: Update the CFML parser first (forked from the abandoned cfparser), publish it as a versioned Java artifact, then upgrade CFLint to depend on it, add dialect flags, an “UnsupportedFeature” rule, and a parse-only discovery mode for CI.

Repositories & naming
Where to host

GitHub is best for open-source visibility, PRs, and easy consumption via JitPack.

Bitbucket is fine for mirrors/internal work, but we’ll publish artifacts from GitHub.

Names (recommended)

Org: peoplewell-cfml (or peoplewell-open if you prefer broader scope)

Parser fork: cfml-parser (a maintained fork/replacement of cfparser)

CFLint fork: cflint (keep the name; it’s recognizable)

Why not keep “cfparser”? To avoid namespace confusion and signal this is a maintained successor. We’ll still attribute the lineage in the README.

Mono-repo vs multiple repos

Community distribution: keep two repos (parser + linter).

Developer ergonomics: create a local composite build so both build together.

Cursor—easiest setup:

Use two repos but add a Gradle composite build in the CFLint repo that includes the parser as a sub-checkout. This gives you cross-project navigation, symbol search, and tests in one workspace without sacrificing clean publishing.

Branching, versioning & distribution

Branches:

main = stable,

dev = integration branch,

feature branches feat/*, fix branches fix/*.

Versioning:

Parser: start at 2.12.0-peoplewell.0 and increment suffixes .1, .2, …

CFLint: bump minor when the parser changes (e.g., 1.6.0-peoplewell.0).

Artifact publishing (parser):

Start with JitPack for speed (consumed via com.github.peoplewell-cfml:cfml-parser:<tag>).

Later, promote to Maven Central (new groupId, e.g., com.peoplewell.cfml).

Milestones (high-level)

Parser fork online (builds locally; published by JitPack)

CFLint consuming forked parser (builds; CLI runs)

Dialect/version support (--dialect, .cflintrc)

Tolerant parsing (custom error strategy + UnknownSyntax findings)

Parse-only CI pass to discover gaps in your codebase

UnsupportedFeature rule (data-driven matrix)

PRs upstream (optionally) and ForgeBox packaging for CFLint CLI

Detailed Task List (for Cursor)
0) Workspace bootstrap

Cursor: Create a new workspace with two folders checked out side-by-side:

cfml-parser/ (new repo)

cflint/ (forked)

Parser repo initialization

Create cfml-parser repo with:

Gradle build (build.gradle.kts) or Maven POM (choose Gradle for consistency with CFLint).

settings.gradle.kts

src/main/antlr/ for grammars (import from upstream cfparser)

src/main/java/ generated parser package

src/test/java/ JUnit tests

README.md noting lineage and license

Import the existing cfparser grammars/code as the baseline.

If the legacy project used Maven/ANTLR v3, keep the current ANTLR major initially to reduce scope. We’ll modernize tokens/rules first.

CFLint repo prep

Fork upstream CFLint into cflint/.

Ensure it builds locally (Gradle).

Add composite build to include parser during development:

In CFLint settings.gradle, add:

includeBuild('../cfml-parser')


In CFLint modules that depend on parser, replace the external dependency with implementation(project(':')) while in dev (we’ll keep a profile to switch back to published coordinates for releases).

1) Publishing from parser via JitPack

Cursor: Add JitPack metadata.

Ensure group and version are set in cfml-parser/build.gradle.kts:

group = "com.github.peoplewell-cfml"
version = "2.12.0-peoplewell.0"


Tag the repo v2.12.0-peoplewell.0.

In CFLint’s build.gradle, add:

repositories {
  mavenCentral()
  maven { url "https://jitpack.io" }
}
dependencies {
  implementation "com.github.peoplewell-cfml:cfml-parser:v2.12.0-peoplewell.0"
}


During development you’ll use the composite build instead; for CI/release you’ll use the JitPack coordinate.

2) Dialect/version support in CFLint

Cursor:

2.1 Add a Dialect enum

Create cflint-core/src/main/java/com/cflint/config/Dialect.java:

public enum Dialect {
  ADOBE_2023,
  ADOBE_2025,
  LUCEE_5_4,
  LUCEE_6_0,
  LUCEE_6_1;

  public boolean isAdobe() { return name().startsWith("ADOBE_"); }
  public boolean isLucee() { return name().startsWith("LUCEE_"); }
}

2.2 Thread dialect into context

Extend the CLI to accept --dialect <value> (default ADOBE_2025).

Extend .cflintrc to allow:

{ "dialect": "ADOBE_2025", "tolerant": true, "failOnUnsupported": false, "maxParseErrors": 100 }


Ensure the dialect is accessible to:

Parser bootstrap (if needed)

Rule engine (listeners/visitors)

2.3 Parse-only mode

Add a CLI flag --mode=parse-only that:

Recursively scans --folder (or default CWD) for *.cfm/*.cfc

Calls parser only; collects syntax errors into parse-errors.json

Exits 0 unless --failOnParseErrors is set

3) Tolerant parsing (parser repo)

Cursor:

3.1 Custom error strategy

Create src/main/java/com/peoplewell/cfml/parser/CfmlTolerantErrorStrategy.java:

Extend org.antlr.v4.runtime.DefaultErrorStrategy

Override recover, recoverInline, and sync to:

Report the error via a listener

Consume tokens until a sync boundary: ; ) ] } , NEWLINE </ /> >

Avoid infinite loops

3.2 Error listener

Create CfmlErrorListener.java to collect:

file, line, column, offending token, rule name

Provide a List<ParseDiagnostic> sink with a per-file cap (100 default)

3.3 Wire into entrypoint

Where you create the parser:

parser.setErrorHandler(new CfmlTolerantErrorStrategy());
parser.removeErrorListeners();
parser.addErrorListener(new CfmlErrorListener(collector));

3.4 (Optional) island rules

For high-breakage zones (unknown tag, attribute, or operator), add guarded “escape” rules that consume until a safe boundary when tolerant is enabled.

3.5 Tests (parser)

Add JUnit tests:

Missing ) recovers at ; and continues

Unknown <cfFoo> tag is skipped and the next statement is parsed

Strange operator token still allows the rest of the file to parse

4) Findings in CFLint

Cursor:

4.1 UnknownSyntax finding

Add a new finding type UnknownSyntax:

Severity: WARN by default

Payload: file, line:col, short message, optional code frame

4.2 UnsupportedFeature rule

Data-driven via resources/dialects.json:

[
  { "id":"script.tagIslands", "label":"Tag islands in CFScript", "adobe":"2023", "lucee":"5.4",
    "message":"Tag islands require Adobe CF 2023+ or Lucee 5.4+." }
]


When walker hits a node flagged with a featureId, compare to selected dialect. If not supported, emit UnsupportedFeature (configurable severity; --failOnUnsupported turns it into build failure).

4.3 CLI/config wire-up

--tolerant (default true)

--max-parse-errors <N> (default 100)

--failOnUnsupported

JSON/FindBugs reporters include the new findings

5) Grammar modernization (parser)

Cursor: Create a conformance corpus under cfml-parser/src/test/resources/corpus/<feature>/… with tiny .cfm/.cfc snippets. Start with what your codebase actually uses (we’ll expand later):

Priority list:

Tag islands in <cfscript>

Operators/expressions added/changed for CF 2023/2025 and Lucee 6.x

Function/attribute additions that broke tokenization

Trailing commas in structs/arrays (if applicable)

Lambda/arrow-ish constructs (if you use them)

Red/Green loop: each failing snippet → add/adjust tokens or alternatives → green.

6) CI integration & discovery run

Cursor: In CFLint repo, add Bitbucket Pipelines (or GitHub Actions) to run a parse-only discovery on your main codebase:

image: eclipse-temurin:17

pipelines:
  default:
    - step:
        name: CFLint Parse-Only Discovery
        caches: [gradle, maven]
        script:
          - ./gradlew :cli:shadowJar
          - java -jar cli/build/libs/CFLint-all.jar \
              --dialect LUCEE_6_1 \
              --mode parse-only \
              --folder ./src \
              --report build/parse-errors.json \
              --tolerant --max-parse-errors 100
        artifacts:
          - build/parse-errors.json


Follow-up step to run full lint once parser stabilizes:

          - java -jar cli/build/libs/CFLint-all.jar \
              --dialect ADOBE_2025 \
              --folder ./src \
              --json --jsonfile build/cflint.json \
              --xml --xmlstyle findbugs --xmlfile build/cflint-findbugs.xml

7) VS Code & CommandBox surface

Cursor:

Ensure CFLint CLI accepts --dialect and reads .cflintrc.

Prepare a small ForgeBox package that bundles the CFLint fat jar for CommandBox users (optional now; easy later).

8) Contribution notes / community plan

Open issues/PRs that explain:

Why a new parser fork (abandonware, CF2023/2025 & Lucee 6.x support, tolerant parsing).

SemVer & artifact coordinates (JitPack now; Maven Central later).

Dialect flags and the new UnsupportedFeature rule.

Offer to upstream changes or maintain a long-term maintained fork.

“Why” summary (for Cursor & reviewers)

Separate parser & linter keeps a clean boundary, enables reuse, and mirrors industry practice.

Dialect flags let teams pin to their runtime (Adobe vs Lucee; version-specific).

Tolerant parsing preserves value from the rest of the file, avoids “all-or-nothing” failures, and accelerates migration.

Parse-only discovery quickly inventories grammar gaps across large codebases.

Data-driven compatibility (dialects.json) avoids code changes for rule tweaks.

Immediate Next Actions (assignable)

Ken

Create GitHub org peoplewell-cfml.

Create repos cfml-parser and cflint (fork).

Give Cursor access to both repositories/workspace.

Cursor

Set up composite build (CFLint includes ../cfml-parser).

Add Dialect enum, CLI flags, .cflintrc support.

Implement tolerant parsing hooks in parser; add 6–8 parser unit tests.

Add UnknownSyntax & UnsupportedFeature findings + reporters.

Add --mode=parse-only and produce parse-errors.json.

Wire Bitbucket/GitHub CI examples into the cflint repo.

If you want, I can also generate seed files (dialects.json, a few test snippets, and minimal Java skeletons for the error strategy and listener).