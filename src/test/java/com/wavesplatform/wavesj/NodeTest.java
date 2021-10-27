package com.wavesplatform.wavesj;

import com.wavesplatform.crypto.Crypto;
import com.wavesplatform.transactions.DataTransaction;
import com.wavesplatform.transactions.account.PrivateKey;
import com.wavesplatform.transactions.common.Amount;
import com.wavesplatform.transactions.common.AssetId;
import com.wavesplatform.transactions.common.Proof;
import com.wavesplatform.transactions.data.StringEntry;
import org.junit.jupiter.api.Test;


import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class NodeTest {
    private final String seed = Crypto.getRandomSeedPhrase();
    private final PrivateKey privateKey = PrivateKey.fromSeed(this.seed);

    @Test
    void testSigning() {
        UUID value = UUID.randomUUID();

        // sut
        DataTransaction dataTransaction = DataTransaction.builder(
                new StringEntry("key",value.toString()))
                .getSignedWith(this.privateKey);

        assertNotNull(dataTransaction);
        List<Proof> proofs = dataTransaction.proofs();
        assertEquals(1,proofs.size());
        assertTrue(this.privateKey.isSignatureValid(dataTransaction.bodyBytes(),proofs.get(0).bytes()));
    }

    @Test
    void testDefaultCalculateFee() {
        UUID value = UUID.randomUUID();

        // sut
        Amount fee = DataTransaction.builder(
                        new StringEntry("key",value.toString()))
                .getSignedWith(this.privateKey)
                .fee();

        // verify it has a Fee
        assertNotNull(fee);
        assertEquals(AssetId.WAVES,fee.assetId());
        assertEquals(100_000,fee.value());

        // the fee seems to be the same if unsigned
        Amount feeUnsigned = DataTransaction.builder(
                        new StringEntry("key",value.toString()))
                .getUnsigned().fee();

        assertEquals(fee,feeUnsigned);
    }

    @Test
    void testOvewriteDefaultFee() {
        UUID value = UUID.randomUUID();

        // sut
        Amount fee = DataTransaction.builder(
                        new StringEntry("key",value.toString()))
                .fee(Amount.of(1337,AssetId.as("WEST")))
                .getSignedWith(this.privateKey)
                .fee();

        // verify the provided fee is set
        assertNotNull(fee);
        assertFalse(fee.assetId().isWaves());
        assertEquals("WEST",fee.assetId().toString());
        assertEquals(1337,fee.value());
    }
}