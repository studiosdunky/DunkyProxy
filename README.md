# DunkyProxy

Proxy da rede Dunky. É um fork do [BungeeCord](https://github.com/SpigotMC/BungeeCord) de md_5, a partir do commit `5430e4a` (27/09/2026), reorganizado no mesmo formato dos outros repositórios da rede.

O código continua nos pacotes `net.md_5.bungee`, então plugins de BungeeCord rodam sem alteração. A licença do BungeeCord está em [LICENSE](LICENSE) e continua valendo para este código.

## Organização

- `src/main/java` e `src/main/resources`: o proxy inteiro. No BungeeCord ele é dividido nos módulos Maven `api`, `chat`, `config`, `dialog`, `event`, `log`, `native`, `nbt`, `protocol`, `proxy`, `query`, `serializer`, `slf4j` e `bootstrap`; aqui todos ficam juntos.
- `src/test`: os testes do BungeeCord.
- `modules/`: os plugins que acompanham o proxy (`/server`, `/glist`, `/send`, `/find`, `/alert`, `/gkick` e a memória do último servidor). Cada um vira um jar na pasta `modules` do servidor.
- `native/`: código C da criptografia e da compressão nativas, usadas só no Linux. Os binários já compilados estão em `src/main/resources`.

## Compilar

O proxy precisa do Java 17 ou mais novo para compilar e para rodar. O `gradle.properties` aponta para um JDK 21 instalado; sem ele o Gradle baixa um.

```
gradlew build
```

Gera `DunkyProxy.jar` e os módulos em `D:/net.dunky/servers/proxy`.

## Atualizar a partir do BungeeCord

Copie por cima as pastas `src/main` e `src/test` de cada módulo do BungeeCord e refaça as alterações próprias deste fork:

- `BungeeCord.getName()` retorna `DunkyProxy`.
- `BungeeCordLauncher` mostra o nome do proxy na linha de versão.
- `ModuleManager` não avisa nem baixa módulos do Jenkins do BungeeCord, porque os módulos são compilados aqui.
- `BungeeCord.reloadMessages` cria o `messages.properties` na pasta do proxy, lê em UTF-8 e aceita cores com `&`. As mensagens embutidas estão em português.
