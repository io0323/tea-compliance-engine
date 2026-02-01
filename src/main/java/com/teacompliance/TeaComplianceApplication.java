package com.teacompliance;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * TeaCompliance Engine アプリケーションのメインクラス
 * 
 * 茶葉ロットのコンプライアンス評価を自動化する業務向けエンジン
 */
@SpringBootApplication
public class TeaComplianceApplication {

    public static void main(String[] args) {
        SpringApplication.run(TeaComplianceApplication.class, args);
    }
}
