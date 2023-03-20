# GitHub actions
## Allgemein
GitHub Actions ist eine Plattform, von GitHub, für Continuous Integration und Continuous Delivery (CI/CD). GitHub Actions kann aufgrund von Ereignisse in einem Repository zuvordefinierte Workflows ausführen. GitHub stellt dafür virtuelle Linux-, Windows- und macOS-Computer in der Cloud für die Ausführung bereit. Alternativ können sie auch selbst gehosted werden. [1]

## CI/CD
"The acronym CI/CD has a few different meanings. The "CI" in CI/CD always refers to continuous integration, which is an automation process for developers. [...] CI means new code changes to an app are regularly built, tested, and merged to a shared repository. [...]

The final stage of a mature CI/CD pipeline is continuous deployment. [...] continuous deployment automates releasing an app to production.
" [2]

Häufige Aufgaben von CI/CD sind:
- Unit Tests
- Kompelieren von Applikationen
- Deployment der Applikationen auf production

## GitHub actions ins im Detail

Der Workflow enthält einen oder mehrere Aufträge, die nacheinander oder gleichzeitig ausgeführt werden können. Jeder Auftrag wird innerhalb eines eigenen Runners der VM oder in einem Container ausgeführt und verfügt über einen oder mehrere Schritte. Diese führen entweder vordefinierte Skripts oder eine Aktion aus.

```yaml
jobs:
  test:
    runs-on: ubuntu-latest
    steps:
     - name: demonstration action
       uses: actions/checkout@v3
     - name: demonstration script
       run: echo "Hello World"
```

### Aktionen
"An action is a custom application for the GitHub Actions platform that performs a complex but frequently repeated task. [...] An action can pull your git repository from GitHub, set up the correct toolchain for your build environment, or set up the authentication to your cloud provider." [3]

Aktionen sind vorgefertigte kleine Programme die in einem GitHub Actions Workflow ausgeführt werden, um allgemeine Aufgaben durchzuführen. Mancher dieser Aktionen sind öffentlich unter [Aktionen](https://github.com/marketplace?type=actions) verfügbar.

In GitHub Actions werden Workflows mithilfe der YAML-Syntax definiert. Die einzelnen Workflows werden in deinem Coderepository jeweils als separate YAML-Datei in einem Verzeichnis mit dem Namen `.github/workflows` gespeichert.

### Beispiel
Es soll eine kleine Java Spring App mit Github Action automatisch, beim pushen auf ein Git Repository, getestet werden. Getestet wird folgende Funktion die zwei Zahlen addiert.

```java
public class MathHelper {
    // adds two numbers and returns the sum
    public  static Integer add(int a, int b){
        return a + b;
    }
}
```
Der Unit-Test sieht folgendermaßen aus:
```java
class MathHelperTest {
    @Test
    void add() {
        int a = 1;
        int b = 1;
        Assertions.assertEquals(a+b, MathHelper.add(a,b));
    }
}
```
Die Spring Anwendung verwendet Maven für das Dependency-Managment. Deshalb muss die Anwendung über den Befehl `mvn ...` gesteuert werden.


Zuerst wird die Umgebung für das Testen vorbereitet.


Als erster Schritt wird das Git Repository von Github über diese Action auf den Github Action-Runner geklont.
```yaml
...
- name: Checkout
  uses: actions/checkout@v3
...
```
Danerach wird über eine Github Aktion Java Version 17 installiert. Durch die Angabe des verwendeten project management Tools, kann die Installation durch caching beschleunigt werden.
```yaml
...
- name: Set up JDK 17
  uses: actions/setup-java@v3
  with:
    java-version: '17'
    distribution: 'adopt'
    cache: maven
...
```

Erst jetzt wird die Anwendung getestet, wenn ein Fehler auftritt, dann wird der Workflow abgebrochen und in Github als Fehlgeschlagen makiert.
```yaml
...
- name: Test
  run: mvn --batch-mode --update-snapshots verify
...
```

## Extra Zuckerl
### Docker-Container
Wenn für die Ci/Cd Pipeline extra Abhängigkeiten wie z.B. Datenbanken (MySQL, Postgresql, MongoDB, ...) oder anderen Microservices benötigt werden, können diese durch Docker-Container zur Verfügung gestellt werden (Um Docker verwenden zu können, muss ein Runner mit Linux Betriebssystem verwendet werden).

Folgende Datei mit dem Namen `docker-compose.yml` muss in das Git Repository hinzugefügt werden.
```yaml
version: '3.8'
services:
  database:
    container_name: ci-cd-test_postgres
    image: postgres:15.1-alpine
```

Dann kann mit dem Folgenden Schritt, nach dem git clone, alle Docker-Container gestartet werden.
```yaml
...
- name: Start Docker-Container
  run: docker-compose up -d
..
```

### Enviroment Variables
Für manche Schritte im Workflow werden Umgebungs-Variablen benötigt. Diese können zu jedem Schritt hinzugefügt werden.
```yaml
...
- name: Greet person
  run: echo "hello $NAME"
  env:
    NAME: Sengseis
...
```

1. https://docs.github.com/en/actions/learn-github-actions/understanding-github-actions
2. https://www.redhat.com/en/topics/devops/what-is-ci-cd
3. https://docs.github.com/en/actions/learn-github-actions/understanding-github-actions