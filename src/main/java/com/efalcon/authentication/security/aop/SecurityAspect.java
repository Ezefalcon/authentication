package com.efalcon.authentication.security.aop;

import com.efalcon.authentication.annotation.SameUserOrAdminAccessOnly;
import com.efalcon.authentication.model.Role;
import com.efalcon.authentication.model.dto.UserTokenDto;
import com.efalcon.authentication.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PathVariable;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

/**
 * Created by efalcon
 */
@Component
@Aspect
@Profile({"!test", "!dev"})
public class SecurityAspect {

    @Before("@within(sameUserOrAdminAccessOnly) || @annotation(sameUserOrAdminAccessOnly)")
    public void sameUserAccessOnlyOrAdmin(JoinPoint joinPoint,
                                          com.efalcon.authentication.annotation.SameUserOrAdminAccessOnly sameUserOrAdminAccessOnly)
            throws IllegalAccessException {
        UserTokenDto authenticatedUser = (UserTokenDto) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (!authenticatedUser.getId().equals(getIdFromPathVariable(joinPoint, sameUserOrAdminAccessOnly)) && !authenticatedUser.getRoles().contains(Role.ADMIN)) {
            throw new IllegalAccessException();
        }
    }


    public Long getIdFromPathVariable(JoinPoint joinPoint, SameUserOrAdminAccessOnly sameUserOrAdminAccessOnly) {
        try {
            MethodSignature signature = (MethodSignature) joinPoint.getSignature();
            Method method = signature.getMethod();
            Object[] args = joinPoint.getArgs();
            Annotation[][] parameterAnnotations = method.getParameterAnnotations();

            String paramName = sameUserOrAdminAccessOnly.value();


            // Find the parameter annotated with @PathVariable and matching paramName
            for (int i = 0; i < parameterAnnotations.length; i++) {
                for (Annotation annotation : parameterAnnotations[i]) {
                    if (annotation instanceof PathVariable pathVariable && pathVariable.value().equals(paramName)) {
                        return (Long) args[i];
                    }
                }
            }
            throw new IllegalArgumentException("No such parameter " + paramName);
        } catch (Throwable t) {
            throw new RuntimeException("Error while getting id from parameter", t);
        }
    }
}
