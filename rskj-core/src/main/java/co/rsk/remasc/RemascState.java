/*
 * This file is part of RskJ
 * Copyright (C) 2017 RSK Labs Ltd.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package co.rsk.remasc;


import org.ethereum.util.RLP;
import org.ethereum.util.RLPList;
import org.spongycastle.util.BigIntegers;

import java.math.BigInteger;
import java.util.*;

/**
 * DTO to send the contract state.
 * Not production code, just used for debugging.
 * @author Oscar Guindzberg
 */
public class RemascState {
    private final BigInteger rewardBalance;
    private final BigInteger burnedBalance;
    private final SortedMap<Long, List<Sibling>> siblings;

    private final Boolean brokenSelectionRule;

    public RemascState(BigInteger rewardBalance, BigInteger burnedBalance, SortedMap<Long, List<Sibling>> siblings, Boolean brokenSelectionRule) {
        this.rewardBalance = rewardBalance;
        this.burnedBalance = burnedBalance;
        this.siblings = siblings;
        this.brokenSelectionRule = brokenSelectionRule;
    }

    public BigInteger getRewardBalance() {
        return rewardBalance;
    }

    public BigInteger getBurnedBalance() {
        return burnedBalance;
    }

    public SortedMap<Long, List<Sibling>> getSiblings() {
        return siblings;
    }

    public Boolean getBrokenSelectionRule() {
        return brokenSelectionRule;
    }

    public byte[] getEncoded() {
        byte[] rlpRewardBalance = RLP.encodeBigInteger(this.rewardBalance);
        byte[] rlpBurnedBalance = RLP.encodeBigInteger(this.burnedBalance);
        byte[] rlpSiblings = RemascStorageProvider.getSiblingsBytes(this.siblings);
        byte[] rlpBrokenSelectionRule = new byte[1];

        if (brokenSelectionRule)
            rlpBrokenSelectionRule[0] = 1;
        else
            rlpBrokenSelectionRule[0] = 0;

        return RLP.encodeList(rlpRewardBalance, rlpBurnedBalance, rlpSiblings, rlpBrokenSelectionRule);
    }

    public static RemascState create(byte[] data) {
        RLPList rlpList = (RLPList)RLP.decode2(data).get(0);
        byte[] rlpRewardBalanceBytes = rlpList.get(0).getRLPData();
        byte[] rlpBurnedBalanceBytes = rlpList.get(1).getRLPData();

        BigInteger rlpRewardBalance = rlpRewardBalanceBytes == null ? BigInteger.ZERO : BigIntegers.fromUnsignedByteArray(rlpRewardBalanceBytes);
        BigInteger rlpBurnedBalance = rlpBurnedBalanceBytes == null ? BigInteger.ZERO : BigIntegers.fromUnsignedByteArray(rlpBurnedBalanceBytes);

        SortedMap<Long, List<Sibling>> rlpSiblings = RemascStorageProvider.getSiblingsFromBytes(rlpList.get(2).getRLPData());

        byte[] rlpBrokenSelectionRuleBytes = rlpList.get(3).getRLPData();

        Boolean rlpBrokenSelectionRule;

        if (rlpBrokenSelectionRuleBytes != null && rlpBrokenSelectionRuleBytes.length != 0 && rlpBrokenSelectionRuleBytes[0] != 0)
            rlpBrokenSelectionRule = Boolean.TRUE;
        else
            rlpBrokenSelectionRule = Boolean.FALSE;

        return new RemascState(rlpRewardBalance, rlpBurnedBalance, rlpSiblings, rlpBrokenSelectionRule);
    }

    @Override
    public String toString() {
        return "RemascState{" +
                "rewardBalance=" + rewardBalance +
                ", burnedBalance=" + burnedBalance +
                ", siblings=" + siblings +
                ", brokenSelectionRule=" + brokenSelectionRule +
                '}';
    }
}
