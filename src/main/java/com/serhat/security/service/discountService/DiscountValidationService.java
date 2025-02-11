package com.serhat.security.service.discountService;

import com.serhat.security.entity.DiscountCode;
import com.serhat.security.entity.User;
import com.serhat.security.entity.enums.CouponStatus;
import com.serhat.security.exception.CouponAlreadyUsedException;
import com.serhat.security.exception.DiscountCodeExpiredException;
import com.serhat.security.exception.InvalidDiscountCodeException;
import com.serhat.security.interfaces.DiscountValidationInterface;
import com.serhat.security.repository.DiscountCodeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DiscountValidationService implements DiscountValidationInterface {
    private final DiscountCodeRepository discountCodeRepository;

    @Override
    public void validateDiscountCode(DiscountCode discountCode, User user) {
        if (discountCode.getUser() != null && !discountCode.getUser().getUserId().equals(user.getUserId())) {
            throw new InvalidDiscountCodeException("This discount code is not valid for the current user!");
        }

        if (discountCode.getStatus().equals(CouponStatus.EXPIRED)) {
            throw new DiscountCodeExpiredException("This coupon has expired!");
        }

        if (discountCode.getStatus().equals(CouponStatus.USED)) {
            throw new CouponAlreadyUsedException("The coupon you entered is already used");
        }
    }

    @Override
    public DiscountCode findById(Long id){
        return discountCodeRepository.findById(id)
                .orElseThrow(()-> new DiscountCodeExpiredException("Discount Not Found"));
    }
}
