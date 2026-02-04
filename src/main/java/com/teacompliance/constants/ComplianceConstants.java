package com.teacompliance.constants;

/**
 * コンプライアンス関連の定数クラス
 */
public final class ComplianceConstants {
    
    private ComplianceConstants() {
        // ユーティリティクラスのインスタンス化を防止
    }
    
    // 評価基準値
    public static final double DEFAULT_AROMA_THRESHOLD = 80.0;
    public static final double DEFAULT_MOISTURE_MAX = 10.0;
    public static final double DEFAULT_PESTICIDE_MAX = 5.0;
    
    // バリデーション制約
    public static final int LOT_CODE_MIN_LENGTH = 1;
    public static final int LOT_CODE_MAX_LENGTH = 50;
    public static final int ORIGIN_MAX_LENGTH = 50;
    public static final int VARIETY_MAX_LENGTH = 50;
    public static final double MOISTURE_MIN = 0.0;
    public static final double MOISTURE_MAX = 100.0;
    public static final double PESTICIDE_MIN = 0.0;
    public static final double PESTICIDE_MAX_ALLOWED = 10.0;
    public static final int AROMA_SCORE_MIN = 0;
    public static final int AROMA_SCORE_MAX = 100;
    
    // ロットコード形式
    public static final String LOT_CODE_PATTERN = "^TL-\\d{4}-\\d{3}$";
    public static final String LOT_CODE_FORMAT_EXAMPLE = "TL-2024-001";
    
    // エラーメッセージ
    public static final String ERROR_LOT_CODE_REQUIRED = "ロットコードは必須です";
    public static final String ERROR_LOT_CODE_INVALID = "ロットコードの形式が正しくありません。形式: TL-YYYY-NNN (例: TL-2024-001)";
    public static final String ERROR_ORIGIN_REQUIRED = "産地は必須です";
    public static final String ERROR_VARIETY_REQUIRED = "品種は必須です";
    public static final String ERROR_MOISTURE_REQUIRED = "水分量は必須です";
    public static final String ERROR_PESTICIDE_REQUIRED = "農薬レベルは必須です";
    public static final String ERROR_AROMA_SCORE_REQUIRED = "香りスコアは必須です";
    public static final String ERROR_PRODUCTION_DATE_REQUIRED = "生産日は必須です";
    
    // APIパス
    public static final String API_BASE_PATH = "/api";
    public static final String TEA_LOTS_PATH = "/api/tea-lots";
    public static final String COMPLIANCE_PATH = "/api/compliance";
    public static final String COMPLIANCE_CHECK_PATH = "/api/compliance/check";
    public static final String COMPLIANCE_RULES_PATH = "/api/compliance/rules";
    
    // HTTPステータスメッセージ
    public static final String CONTENT_TYPE_NOT_SUPPORTED = "Content-Type not supported. Please use application/json";
    public static final String VALIDATION_FAILED = "Validation failed";
    public static final String DUPLICATE_TEA_LOT = "Duplicate tea lot";
}
