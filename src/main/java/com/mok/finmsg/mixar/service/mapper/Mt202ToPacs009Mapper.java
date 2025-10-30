package com.mok.finmsg.mixar.service.mapper;

import com.mok.finmsg.mixar.model.mt.MT202;
import com.mok.finmsg.mixar.model.mt.MT202Cov;
import com.mok.finmsg.mixar.model.mx.pacs009.*;
import com.mok.finmsg.mixar.service.util.SwiftFieldUtils;
import org.springframework.stereotype.Component;

import java.util.Collections;

import static com.mok.finmsg.mixar.service.util.TransactionBuilderUtils.*;


@Component
public class Mt202ToPacs009Mapper {

    public PacsDocument convert(MT202 mt202) {
        if (mt202 == null)
            throw new IllegalArgumentException("MT202 cannot be null");

        GroupHeader gh = buildGroupHeader(mt202.getField20(), mt202.getField32A());
        CreditTransferTransactionFI tx = buildFiToFiTransaction(mt202, gh);

        return buildDocument(gh, tx);
    }

    public PacsDocument convertCov(MT202Cov mt202cov) {
        if (mt202cov == null)
            throw new IllegalArgumentException("MT202COV cannot be null");

        GroupHeader gh = buildGroupHeader(mt202cov.getField20(), mt202cov.getField32A());
        CreditTransferTransactionFI tx = buildCovTransaction(mt202cov, gh);

        return buildDocument(gh, tx);
    }

    private CreditTransferTransactionFI buildFiToFiTransaction(MT202 mt202, GroupHeader gh) {
        var tx = baseTransaction(mt202.getField20(), mt202.getField21(), gh, mt202.getField32A());

        // Agents
        tx.setInstgAgt(SwiftFieldUtils.buildAgent009(mt202.getField52A()));
        tx.setInstdAgt(SwiftFieldUtils.buildAgent009(mt202.getField58A()));

        // Optional intermediaries
        setIntermediaries(tx, mt202.getField56A());

        // Debtor / Creditor FIs
        setFiDebtorCreditor(tx, mt202.getField52A(), mt202.getField58A(), mt202.getField57A());

        return tx;
    }

    private CreditTransferTransactionFI buildCovTransaction(MT202Cov mt202cov, GroupHeader gh) {
        var tx = baseTransaction(
                mt202cov.getField20(),
                mt202cov.getField21Cov() != null ? mt202cov.getField21Cov() : mt202cov.getField21(),
                gh,
                mt202cov.getField32A()
        );

        // Agents
        var instgAgt = SwiftFieldUtils.buildAgent009(
                mt202cov.getField52AOverride() != null ? mt202cov.getField52AOverride() : mt202cov.getField52A()
        );
        var instdAgt = SwiftFieldUtils.buildAgent009(mt202cov.getField58A());

        tx.setInstgAgt(instgAgt);
        tx.setInstdAgt(instdAgt);
        setIntermediaries(tx, mt202cov.getField56A());

        // Debtor / Creditor (COV has customer-level info)
        var dbtr = SwiftFieldUtils.buildDbtrFrom50(mt202cov.getField50A(), mt202cov.getField50K());
        if (dbtr == null) dbtr = instgAgt;

        tx.setUltmtDbtr(instgAgt);
        tx.setDbtr(dbtr);
        tx.setDbtrAcct(buildOtherAccount("UNKNOWN-DBTR-ACCT"));
        tx.setDbtrAgt(instgAgt);

        var cdtr = SwiftFieldUtils.buildCdtrFrom59(mt202cov.getField59());
        tx.setCdtr(cdtr);
        tx.setCdtrAgt(instdAgt);

        return tx;
    }

    private PacsDocument buildDocument(GroupHeader gh, CreditTransferTransactionFI tx) {
        FICdtTrf fiTrf = new FICdtTrf();
        fiTrf.setGrpHdr(gh);
        fiTrf.setCdtTrfTxInf(Collections.singletonList(tx));

        PacsDocument doc = new PacsDocument();
        doc.setFiCdtTrf(fiTrf);
        return doc;
    }
}
