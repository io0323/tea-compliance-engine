# TeaCompliance Engine

茶葉ロットが各種規格・法令・社内基準に適合しているかを自動判定する業務向けエンジンです。

## 技術スタック

- Java 21
- Spring Boot 3
- Spring Web（REST API）
- H2 Database（開発用）
- JPA (Hibernate)
- Lombok
- Maven

## 機能概要

### ドメインモデル

#### TeaLot（茶葉ロット）
- id (Long)
- lotCode (String) - ロットコード
- origin (String) - 産地
- variety (String) - 品種
- moisture (double) - 水分量
- pesticideLevel (double) - 残留農薬レベル
- aromaScore (int) - 香りスコア
- producedAt (LocalDate) - 生産日

#### ComplianceRule（コンプライアンスルール）
- id (Long)
- ruleCode (String) - ルールコード
- description (String) - 説明
- ruleType (MOISTURE/PESTICIDE/AROMA) - ルールタイプ
- threshold (double) - 基準値
- operator (>, <, <=, >=) - 比較演算子
- severity (INFO/WARNING/BLOCK) - 重要度

#### ComplianceResult（評価結果）
- id (Long)
- teaLotId (Long) - 茶葉ロットID
- ruleCode (String) - ルールコード
- result (PASS/FAIL) - 評価結果
- severity (INFO/WARNING/BLOCK) - 重要度
- message (String) - 評価メッセージ
- evaluatedAt (LocalDateTime) - 評価日時

## API エンドポイント

### 茶葉ロット管理
- `POST /api/tea-lots` - 茶葉ロット登録
- `GET /api/tea-lots` - 全茶葉ロット取得
- `GET /api/tea-lots/{id}` - IDで茶葉ロット取得
- `GET /api/tea-lots/by-code/{lotCode}` - ロットコードで茶葉ロット取得
- `GET /api/tea-lots/by-origin?origin=産地` - 産地で茶葉ロット検索
- `GET /api/tea-lots/by-variety?variety=品種` - 品種で茶葉ロット検索

### コンプライアンス評価
- `POST /api/compliance/check/{teaLotId}` - 指定ロットに対して全ルールを評価
- `GET /api/compliance/results/{teaLotId}` - 評価結果一覧取得

## 起動方法

```bash
# プロジェクトのビルドと起動
mvn spring-boot:run
```

アプリケーションが起動後、以下のURLにアクセス可能です：

- アプリケーション: http://localhost:8080
- H2 コンソール: http://localhost:8080/h2-console
  - JDBC URL: `jdbc:h2:mem:teacompliance`
  - ユーザー名: `sa`
  - パスワード: （空）

## 初期データ

### 茶葉ロット（5件）
| ロットコード | 産地 | 品種 | 水分量 | 農薬レベル | 香りスコア |
|-------------|------|------|--------|------------|------------|
| TL-2024-001 | 静岡県 | やぶきた | 8.5% | 0.3 ppm | 75点 |
| TL-2024-002 | 鹿児島県 | ゆたかみどり | 9.2% | 0.6 ppm | 68点 |
| TL-2024-003 | 京都府 | 宇治在来 | 7.8% | 0.2 ppm | 82点 |
| TL-2024-004 | 三重県 | かおりわせ | 10.1% | 0.4 ppm | 55点 |
| TL-2024-005 | 奈良県 | なつみどり | 8.9% | 0.1 ppm | 71点 |

### コンプライアンスルール
| ルールコード | 説明 | タイプ | 基準値 | 演算子 | 重要度 |
|-------------|------|-------|--------|--------|--------|
| MOISTURE_001 | 水分量基準（JAS規格） | MOISTURE | 9.0% | <= | BLOCK |
| PESTICIDE_001 | 残留農薬基準（簡易モデル） | PESTICIDE | 0.5 ppm | <= | BLOCK |
| AROMA_001 | 香りスコア基準（社内品質ルール） | AROMA | 60点 | >= | WARNING |
| MOISTURE_002 | 水分量警告基準 | MOISTURE | 8.5% | <= | WARNING |
| AROMA_002 | 香りスコア優良基準 | AROMA | 80点 | >= | INFO |

## 使用例

### 1. 茶葉ロット登録
```bash
curl -X POST http://localhost:8080/api/tea-lots \
  -H "Content-Type: application/json" \
  -d '{
    "lotCode": "TL-2024-006",
    "origin": "福岡県",
    "variety": "さえみどり",
    "moisture": 8.2,
    "pesticideLevel": 0.25,
    "aromaScore": 78,
    "producedAt": "2024-05-28"
  }'
```

### 2. コンプライアンスチェック実行
```bash
curl -X POST http://localhost:8080/api/compliance/check/1
```

### 3. 評価結果取得
```bash
curl http://localhost:8080/api/compliance/results/1
```

## 設計方針

### Strategy パターンによるルール評価
- 各ルールタイプ（水分量、残留農薬、香りスコア）に専用の評価戦略を実装
- 新しいルールタイプの追加が容易
- 評価ロジックの分離により保守性向上

### レイヤードアーキテクチャ
- Controller: API エンドポイントの定義
- Service: 業務ロジックの実装
- Repository: データアクセス
- Domain: ドメインモデル

### 拡張性
- 新しいルールタイプの追加: `RuleEvaluationStrategy` の実装クラスを追加
- 新しい比較演算子の追加: `ComparisonOperator` enum に追加
- 新しい重要度レベルの追加: `Severity` enum に追加

## ライセンス

このプロジェクトはデモンストレーション用です。
