# プロジェクト概要

このプロジェクトは、SNSなどのプラットフォームに投稿されるメッセージの内末尾に半角または全角の数字が付いているものについて、何か特定のものをインクリメントしていると解釈し、数字が1ずつ増やされているかを監視するアプリケーションである。

## 目的

- 数字が1ずつ増やされているかを検証する
- 以下のようなことが発生した場合にプラットフォーム側へ通知する
  - 数字が一度に2以上増やされた
  - 同じ数字のメッセージが2回以上見つかった

## 目的でないもの

- 数字を増やすメッセージを自ら投稿する

# 技術スタック

- language
  - Scala
- build tool
  - sbt
- library
  - DI container
    - Airframe DI
  - Effect System
    - cats-effect
    - fs2
  - http
    - sttp
      - sttp on fs2
  - testing
    - scalatest
    - scalacheck

# アーキテクチャ

sbtのマルチプロジェクトビルドを利用する

レイヤードアーキテクチャを採用し、レイヤーごとにsbtのサブプロジェクトを作成する

## サブプロジェクト

### domain

数字を増やす際のルールの検証を行うState Machineを実装する。

他のドメインでも登場し得るより抽象的な概念はlibに実装する。

#### 原則

- Scalaの標準ライブラリにのみ依存する
- primitiveな型をラップするValue Objectはopaque typeを使いnewtypeパターンで実装する
- immutableなデータ構造として定義する
- 純粋関数であるメソッドのみを実装する

### application

一般的なusecase層に該当し、以下のようなものを実装する

- infrastructure層に実装するクラスのインターフェース
- 以下のものを統合しアプリケーションのロジックを実装するusecase
  - domain層のコード
  - infrastructure層のインターフェース
  - fs2のAPI

#### 原則

- domainサブプロジェクトにのみ依存する
- SNSの投稿のURLなどアプリケーションのロジックに必要だがコアとなるドメインのロジックに関わらないデータは`context`以下のパッケージに定義する
- infrastructure層に定義するクラスのインターフェースは`integration`以下のパッケージに定義する
- usecaseのクラスの実装
  - `usecase`以下のパッケージに実装し、そのアプリケーションが依存するプラットフォームなどの名前をパッケージ名に入れる
  - `cats.effect.kernel.Async`を使い非同期処理の型を抽象化してF[Unit]を返すようにする
  - プロセスの外部とやりとりする処理はusecaseには書かず、`integration`以下のパッケージにインターフェースを定義して`infrastructure`パッケージに書く

### infrastructure

HTTP通信やDBとのやりとりなど技術的な詳細となるクラスを実装する

#### 原則

- applicationサブプロジェクトにのみ依存する
- 新しいクラスを実装する場合、必ずapplicationサブプロジェクトのintegration以下に同じパッケージ構造でインターフェースを定義し、それを継承する
- 非同期処理の型は`cats.effect.kernel.Async`を使い抽象化する

### di

usecaseサブプロジェクトのintegration以下のパッケージにあるインターフェースとinfrastructure層のクラスに依存し、DIコンテナであるAirframe DIのDesignを合成可能な形で定義する

### entrypoint

diサブプロジェクトに定義したDesignからapplicationサブプロジェクトに定義したusecaseのインスタンスを作成し実行するアプリケーションのエントリポイントとなるsingleton objectを定義する

このサブプロジェクトのsingleton object一つ一つがビルドの単位となる

# ビルド

sbt compile

# テスト

sbt test

# AIエージェント向け指示

- 実装前に関連するサブプロジェクトのコードを確認する
- 新しい依存関係を勝手に追加しない
- 実装後は`sbt test`を実行し、コンパイルやテストが失敗した場合は修正する
- アーキテクチャ上の判断に迷う場合は既存コードを優先する

