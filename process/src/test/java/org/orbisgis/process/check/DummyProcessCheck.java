package org.orbisgis.process.check;

import groovy.lang.Closure;
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

    public LinkedHashMap<String, Object> processInData;
    public Action failAction;
    public String failMessage;
    public Action successAction;
    public String successMessage;
    public IInOutPut[] inputOrOutput;
    public Closure<?> closure;
    public IProcess process;

    public DummyProcessCheck(IProcess process) {
        this.process = process;
    }

    @Override
    public boolean run(LinkedHashMap<String, Object> processInData) {
        this.processInData = processInData;
        return false;
    }

    @Override
    public void onFail(Action action, String message) {
        this.failAction = action;
        this.failMessage = message;
    }

    @Override
    public void onFail(String message) {
        this.failMessage = message;
    }

    @Override
    public void onSuccess(Action action, String message) {
        this.successAction = action;
        this.successMessage = message;
    }

    @Override
    public void onSuccess(String message) {
        this.successMessage = message;
    }

    @Override
    public void setInOutPuts(IInOutPut... inputOrOutput) {
        this.inputOrOutput = inputOrOutput;
    }

    @Override
    public LinkedList<IInOutPut> getInOutPuts() {
        return new LinkedList<>();
    }

    @Override
    public void setClosure(Closure<?> cl) {
        this.closure = cl;
    }

    @Override
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

    @Override
    public Optional<IProcess> getProcess() {
        return Optional.ofNullable(process);
    }
}
