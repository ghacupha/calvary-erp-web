package io.github.calvary.erp;

import io.github.calvary.erp.queue.TransactionEntryMessage;
import io.github.calvary.repository.BalanceSheetItemTypeRepository;
import io.github.calvary.repository.TransactionAccountRepository;
import io.github.calvary.service.BalanceSheetItemValueService;
import io.github.calvary.service.TransactionEntryService;
import io.github.calvary.service.dto.BalanceSheetItemTypeDTO;
import io.github.calvary.service.dto.BalanceSheetItemValueDTO;
import io.github.calvary.service.mapper.BalanceSheetItemTypeMapper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.concurrent.atomic.AtomicReference;

@Service
public class BalanceSheetItemUpdateService implements BalanceSheetUpdateService {

    private final TransactionEntryService transactionEntryService;
    private final BalanceSheetItemValueService balanceSheetItemValueService;
    private final TransactionAccountRepository transactionAccountRepository;
    private final BalanceSheetItemTypeRepository balanceSheetItemTypeRepository;
    private final BalanceSheetItemTypeMapper balanceSheetItemTypeMapper;

    public BalanceSheetItemUpdateService(TransactionEntryService transactionEntryService, BalanceSheetItemValueService balanceSheetItemValueService, TransactionAccountRepository transactionAccountRepository, BalanceSheetItemTypeRepository balanceSheetItemTypeRepository, BalanceSheetItemTypeMapper balanceSheetItemTypeMapper) {
        this.transactionEntryService = transactionEntryService;
        this.balanceSheetItemValueService = balanceSheetItemValueService;
        this.transactionAccountRepository = transactionAccountRepository;
        this.balanceSheetItemTypeRepository = balanceSheetItemTypeRepository;
        this.balanceSheetItemTypeMapper = balanceSheetItemTypeMapper;
    }

    @Async
    @Override
    public void update(TransactionEntryMessage message) {

        transactionEntryService.findOne(message.getId()).ifPresent(transactionEntryDTO -> {
            BalanceSheetItemTypeDTO itemTypeDTO = getBalanceSheetItem(message.getTransactionAccountId());

            BalanceSheetItemValueDTO itemValueDTO = new BalanceSheetItemValueDTO();

            itemValueDTO.setShortDescription(transactionEntryDTO.getDescription());
            // TODO Add date to entries
            itemValueDTO.setEffectiveDate(LocalDate.now());
            // TODO update account posting
            itemValueDTO.setItemAmount(transactionEntryDTO.getEntryAmount());

            itemValueDTO.setItemType(itemTypeDTO);

            balanceSheetItemValueService.save(itemValueDTO);
        });
    }

    private BalanceSheetItemTypeDTO getBalanceSheetItem(Long transactionAccountId) {

        AtomicReference<BalanceSheetItemTypeDTO> typeDTO = new AtomicReference<>();

        transactionAccountRepository.findById(transactionAccountId).flatMap(balanceSheetItemTypeRepository::findBalanceSheetItemTypeByTransactionAccountEquals).ifPresent(itemType -> {
            typeDTO.set(balanceSheetItemTypeMapper.toDto(itemType));
        });

        return typeDTO.get();
    }
}
