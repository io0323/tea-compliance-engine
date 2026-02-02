package com.teacompliance.exception;

/**
 * 茶葉ロットが重複している場合の例外
 */
public class DuplicateTeaLotException extends TeaComplianceException {
    
    public DuplicateTeaLotException(String lotCode) {
        super("TC_002", "茶葉ロットが既に存在します: ロットコード=" + lotCode);
    }
}
