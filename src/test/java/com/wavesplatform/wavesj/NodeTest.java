package com.wavesplatform.wavesj;

import com.wavesplatform.transactions.DataTransaction;
import com.wavesplatform.transactions.account.PrivateKey;
import com.wavesplatform.transactions.common.Proof;
import com.wavesplatform.transactions.data.StringEntry;
import org.junit.jupiter.api.Test;


import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class NodeTest {

    @Test
    void testSigning() {
        PrivateKey privateKey = PrivateKey.fromSeed("test");

        // sut
        DataTransaction dataTransaction = DataTransaction.builder( new StringEntry("key","value")).getSignedWith(privateKey);

        assertNotNull(dataTransaction);
        List<Proof> proofs = dataTransaction.proofs();
        assertEquals(1,proofs.size());
        assertTrue(privateKey.isSignatureValid(dataTransaction.bodyBytes(),proofs.get(0).bytes()));
    }
}