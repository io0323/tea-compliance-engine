package com.teacompliance.exception;

/**
 * 茶葉ロットが見つからない場合の例外
 */
public class TeaLotNotFoundException extends TeaComplianceException {
    
    public TeaLotNotFoundException(Long id) {
        super("TC_001", "茶葉ロットが見つかりません: ID=" + id);
    }
    
    public TeaLotNotFoundException(String lotCode) {
        super("TC_001", "茶葉ロットが見つかりません: ロットコード=" + lotCode);
    }
}
