package com.mibo2000.codigo.codetest.business.pub;

import com.mibo2000.codigo.codetest.BaseResponse;
import com.mibo2000.codigo.codetest.business.BaseBusiness;
import com.mibo2000.codigo.codetest.mapper.CountryMapper;
import com.mibo2000.codigo.codetest.modal.entity.CountryEntity;
import com.mibo2000.codigo.codetest.modal.repo.CountryRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Slf4j
@RequiredArgsConstructor
public class PubBusiness extends BaseBusiness implements IPub {
    private final CountryRepo countryRepo;
    private final CountryMapper countryMapper;

    @Override
    public BaseResponse<?> getCountryList() {
        List<CountryEntity> countryEntityList = this.countryRepo.findAll(Sort.by(Sort.Direction.ASC, "countryName"));
        return BaseResponse.success(this.countryMapper.toDtoList(countryEntityList));
    }
}
