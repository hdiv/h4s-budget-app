package io.budgetapp.model.form.budget;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import io.budgetapp.model.Category;

import javax.validation.constraints.NotNull;

import org.hdiv.services.SecureIdContainer;
import org.hdiv.services.TrustAssertion;

import java.io.Serializable;

/**
 *
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class AddBudgetForm extends BudgetForm implements Serializable, SecureIdContainer {

    private static final long serialVersionUID = 1610773759602819442L;

    @TrustAssertion(idFor=Category.class)
    private Long categoryId;

    @NotNull(message = "{validation.category.required}")
    public Long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }
}
