package com.lec.packages.service;

import java.util.List;

import com.lec.packages.dto.PageRequestDTO;
import com.lec.packages.dto.PageResponseDTO;
import com.lec.packages.dto.TransferHistoryDTO;

public interface RevenueService {

	PageResponseDTO<TransferHistoryDTO> getTransferHistoryByMemId(String memId, PageRequestDTO pageRequestDTO);
}
