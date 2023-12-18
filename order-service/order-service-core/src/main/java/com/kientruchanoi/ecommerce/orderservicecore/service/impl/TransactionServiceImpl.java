package com.kientruchanoi.ecommerce.orderservicecore.service.impl;

import com.kientruchanoi.ecommerce.baseservice.payload.response.BaseResponse;
import com.kientruchanoi.ecommerce.baseservice.payload.response.PageResponse;
import com.kientruchanoi.ecommerce.baseservice.payload.response.ResponseFactory;
import com.kientruchanoi.ecommerce.orderservicecore.entity.Order;
import com.kientruchanoi.ecommerce.orderservicecore.entity.Transaction;
import com.kientruchanoi.ecommerce.orderservicecore.entity.Wallet;
import com.kientruchanoi.ecommerce.orderservicecore.exception.APIException;
import com.kientruchanoi.ecommerce.orderservicecore.mapper.TransactionMapper;
import com.kientruchanoi.ecommerce.orderservicecore.repository.OrderRepository;
import com.kientruchanoi.ecommerce.orderservicecore.repository.TransactionRepository;
import com.kientruchanoi.ecommerce.orderservicecore.repository.WalletRepository;
import com.kientruchanoi.ecommerce.orderservicecore.service.CommonService;
import com.kientruchanoi.ecommerce.orderservicecore.service.TransactionService;
import com.kientruchanoi.ecommerce.orderservicecore.service.WalletService;
import com.kientruchanoi.ecommerce.orderserviceshare.enumerate.TransactionType;
import com.kientruchanoi.ecommerce.orderserviceshare.payload.response.PageResponseTransaction;
import com.kientruchanoi.ecommerce.orderserviceshare.payload.response.TransactionResponse;
import com.kientruchanoi.ecommerce.productserviceshare.payload.response.PageResponseProduct;
import com.kientruchanoi.ecommerce.productserviceshare.payload.response.ProductResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TransactionServiceImpl implements TransactionService {

    private final ResponseFactory responseFactory;
    private final CommonService commonService;
    private final TransactionRepository transactionRepository;
    private final WalletRepository walletRepository;
    private final TransactionMapper transactionMapper;
    private final WalletService walletService;
    private final OrderRepository orderRepository;

    @Override
    public void create(TransactionType type, String des, List<String> orderIds, Wallet wallet, double amount) {
        transactionRepository.save(
                Transaction.builder()
                        .orderIds(orderIds)
                        .type(type.name())
                        .walletId(wallet.getId())
                        .balance(wallet.getBalance())
                        .createdDate(LocalDateTime.now())
                        .amount(amount)
                        .description(des)
                        .build()
        );
    }

    @Override
    public ResponseEntity<BaseResponse<TransactionResponse>> detail(String id) {
        Transaction transaction = transactionRepository.findById(id)
                .orElseThrow(() -> new APIException(HttpStatus.NOT_FOUND, "Không tìm thấy lịch sử giao dịch"));

        Order order = orderRepository.findById(transaction.getOrderIds().get(0))
                .orElseThrow(() -> new APIException(HttpStatus.BAD_REQUEST, "Lỗi hệ thống"));

        String currentUserId = commonService.getCurrentUserId();
        if (!currentUserId.equals(order.getSellerId()) && !currentUserId.equals(order.getCustomerId())) {
            throw new APIException(HttpStatus.UNAUTHORIZED, "Không được phép truy cập.");
        }

        return responseFactory.success("Success", transactionMapper.entityToResponse(transaction));
    }

    @Override
    public ResponseEntity<BaseResponse<List<TransactionResponse>>> byOwner() {
        String userId = commonService.getCurrentUserId();
        Wallet wallet = walletRepository.findByUserId(userId)
                .orElse(walletService.walletBuilder(userId));
        wallet = walletRepository.save(wallet);

        List<TransactionResponse> response = transactionRepository.findAllByWalletId(wallet.getId())
                .stream().map(t -> transactionMapper.entityToResponse(t))
                .collect(Collectors.toList());

        response.sort(Comparator.comparing(TransactionResponse::getCreatedDate));
        Collections.reverse(response);
        return responseFactory.success("Success", response);
    }

    @Override
    public ResponseEntity<BaseResponse<PageResponseTransaction>> allByAdmin(Integer pageNo, Integer pageSize, String sortBy, String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name())
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();

        Pageable pageable = PageRequest.of(pageNo, pageSize, sort);
        Page<Transaction> page = transactionRepository.findAll(pageable);
        return responseFactory.success("Success", paging(page));
    }

    private PageResponseTransaction paging(Page<Transaction> page) {
        List<TransactionResponse> transactions = page.getContent().stream()
                .map(t -> transactionMapper.entityToResponse(t))
                .toList();
        transactions.sort(Comparator.comparing(TransactionResponse::getCreatedDate));
        Collections.reverse(transactions);

        return (PageResponseTransaction) PageResponseTransaction.builder()
                .pageNo(page.getNumber())
                .pageSize(transactions.size())
                .content(transactions)
                .totalPages(page.getTotalPages())
                .totalItems((int) page.getTotalElements())
                .last(page.isLast())
                .build();
    }
}
