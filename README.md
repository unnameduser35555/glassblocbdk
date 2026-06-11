# Path Cleaner

Minecraft Java Edition 1.21.11 用 Fabric MOD です。

棒を持って Dirt Path を右クリックすると、その連結領域にある Dirt Path を BFS で走査し、Grass Block に置き換えます。処理はサーバー側のみで実行され、最大 100000 ブロックで停止します。

## 要件

- Java 21
- Fabric Loader 0.18.6
- Fabric API 0.141.4+1.21.11

## ビルド

```bash
./gradlew build
```

生成物は `build/libs` に出力されます。

## GitHub Actions

`.github/workflows/build.yml` に push 時ビルドを設定しています。