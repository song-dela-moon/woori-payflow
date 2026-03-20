package com.card.payment.clearingsettlement.client;

import com.card.payment.clearingsettlement.dto.TransferRequest;
import com.card.payment.clearingsettlement.dto.TransferResponse;

// 은행 호출 클라이언트
public interface BankTransferClient {
    TransferResponse requestTransfer(TransferRequest request);
}