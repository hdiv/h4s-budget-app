package io.budgetapp.model.form.budget;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import io.budgetapp.model.Budget;

import javax.validation.constraints.NotNull;

import org.hdiv.services.SecureIdContainer;
import org.hdiv.services.TrustAssertion;

import java.io.Serializable;

/**
 *
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class UpdateBudgetForm extends BudgetForm implements Serializable, SecureIdContainer {

    private static final long serialVersionUID = 7677505567308081026L;

    @TrustAssertion(idFor=Budget.class)
    private Long id;
    private double actual;

    @NotNull(message = "{validation.id.required}")
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public double getActual() {
        return actual;
    }

    public void setActual(double actual) {
        this.actual = actual;
    }
}
