# Find Translation (WebStorm / IntelliJ plugin)

Plugin přidává:

1. **Ctrl/Cmd+Click navigaci** z i18n klíčů v `*.tsx`, `*.jsx`, `*.html`, `*.ts`, `*.js` (konfigurovatelné v Settings) do deklarace v `*.json`, `*.yml`, `*.yaml`.
2. Rozpoznání patternů:
   - React/JS/TS string literal: `t('pages.incomeAssessment.confirm')` (klíč je string literal)
   - Angular šablony: `{{ 'pages.incomeAssessment.confirm' | translate }}`
3. Akci **Find Translation Key** (v menu **Find**) pro přímé vyhledání translation klíče.

## Konfigurace

`Settings / Preferences -> Tools -> Find Translation`

- **Source file extensions**: výchozí `tsx,jsx,html,ts,js`
- **Translation file extensions**: výchozí `json,yml,yaml`

## Jak plugin nainstalovat ve WebStorm

### 1) Build ZIP balíčku pluginu
V kořeni projektu spusť:

```bash
./gradlew buildPlugin
```

Výsledný ZIP bude v:

```text
build/distributions/find-translation-0.1.0.zip
```

### 2) Instalace ve WebStorm
1. Otevři **WebStorm**.
2. Jdi do **Settings/Preferences -> Plugins**.
3. Klikni na ozubené kolečko (⚙️) vedle vyhledávání pluginů.
4. Zvol **Install Plugin from Disk...**
5. Vyber ZIP: `build/distributions/find-translation-0.1.0.zip`
6. Potvrď instalaci a **restartuj WebStorm**.

### 3) Použití
- Podrž `Cmd` (macOS) nebo `Ctrl` (Windows/Linux) a klikni na i18n klíč.
- Pro ruční vyhledání klíče použij menu **Find -> Find Translation Key**.

> Poznámka: IntelliJ platform standardně neumožňuje jednoduše přidat nový „tab“ přímo do systémového dialogu `Cmd+Shift+F` (Find in Files). Proto je zde přidaná samostatná akce v menu **Find**.
