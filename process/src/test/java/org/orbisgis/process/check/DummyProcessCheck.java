package org.orbisgis.process.check;

import groovy.lang.Closure;
import org.orbisgis.commons.annotations.NotNull;
import org.orbisgis.commons.annotations.Nullable;
import org.orbisgis.process.api.IProcess;
import org.orbisgis.process.api.check.IProcessCheck;
import org.orbisgis.process.api.inoutput.IInOutPut;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Optional;

/**
 * {@link IProcessCheck} implementation for test purpose.
 *
 * @author Erwan Bocher (CNRS)
 * @author Sylvain PALOMINOS (UBS 2020)
 */
public class DummyProcessCheck implements IProcessCheck {

    @Nullable
    public LinkedHashMap<String, Object> processInData;
    @Nullable
    public Action failAction;
    @Nullable
    public String failMessage;
    @Nullable
    public Action successAction;
    @Nullable
    public String successMessage;
    @Nullable
    public IInOutPut[] inputOrOutput;
    @Nullable
    public Closure<?> closure;
    @Nullable
    public IProcess process;

    public DummyProcessCheck(@Nullable IProcess process) {
        this.process = process;
    }

    @Override
    public boolean run(@Nullable LinkedHashMap<String, Object> processInData) {
        this.processInData = processInData;
        return false;
    }

    @Override
    public void onFail(@Nullable Action action, @Nullable String message) {
        this.failAction = action;
        this.failMessage = message;
    }

    @Override
    public void onFail(@Nullable String message) {
        this.failMessage = message;
    }

    @Override
    public void onSuccess(@Nullable Action action, @Nullable String message) {
        this.successAction = action;
        this.successMessage = message;
    }

    @Override
    public void onSuccess(@Nullable String message) {
        this.successMessage = message;
    }

    @Override
    public void setInOutPuts(@Nullable IInOutPut... inputOrOutput) {
        this.inputOrOutput = inputOrOutput;
    }

    @NotNull
    @Override
    public LinkedList<IInOutPut> getInOutPuts() {
        return new LinkedList<>();
    }

    @Override
    public void setClosure(@Nullable Closure<?> cl) {
        this.closure = cl;
    }

    @Override
    @NotNull
    public Optional<Closure<?>> getClosure() {
        return Optional.ofNullable(closure);
    }

    @Override
    public boolean fail() {
        return false;
    }

    @Override
    public boolean success() {
        return false;
    }

    @NotNull
    @Override
    public Optional<IProcess> getProcess() {
        return Optional.ofNullable(process);
    }
}
