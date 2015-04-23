        var result = QueryExecute("
            SELECT info_card_id, document_num, revision_nm, title_nm
            FROM tdc_doc_infocard
            WHERE info_card_id = :sourceTemplateNum
            ORDER BY UPPER(document_num) ASC, revision_seq DESC
        ", { sourceTemplateNum = { value="#sourceTemplateNum#", cfsqltype="cf_sql_varchar" }});