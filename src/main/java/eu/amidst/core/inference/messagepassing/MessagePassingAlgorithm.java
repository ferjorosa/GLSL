/*
 *
 *
 *    Licensed to the Apache Software Foundation (ASF) under one or more contributor license agreements.
 *    See the NOTICE file distributed with this work for additional information regarding copyright ownership.
 *    The ASF licenses this file to You under the Apache License, Version 2.0 (the "License"); you may not use
 *    this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *            http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software distributed under the License is
 *    distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and limitations under the License.
 *
 *
 */

package eu.amidst.core.inference.messagepassing;

import eu.amidst.core.distribution.UnivariateDistribution;
import eu.amidst.core.exponentialfamily.EF_BayesianNetwork;
import eu.amidst.core.exponentialfamily.EF_UnivariateDistribution;
import eu.amidst.core.inference.InferenceAlgorithm;
import eu.amidst.core.models.BayesianNetwork;
import eu.amidst.core.utils.Vector;
import eu.amidst.core.variables.Assignment;
import eu.amidst.core.variables.HashMapAssignment;
import eu.amidst.core.variables.Variable;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * This class implements the interface {@link InferenceAlgorithm} and defines the Message Passing algorithm.
 */
public abstract class MessagePassingAlgorithm<E extends Vector> implements InferenceAlgorithm, Serializable{

    static org.slf4j.Logger logger = LoggerFactory.getLogger(MessagePassingAlgorithm.class);

    /** Represents the serial version ID for serializing the object. */
    private static final long serialVersionUID = 4107783324901370839L;

    /** Represents the {@link BayesianNetwork} model. */
    protected BayesianNetwork model;

    /** Represents the {@link EF_BayesianNetwork} model. */
    protected EF_BayesianNetwork ef_model;

    /** Represents an {@link Assignment} object. */
    protected Assignment assignment = new HashMapAssignment(0);

    /** Represents the list of {@link Node}s. */
    transient protected List<Node> nodes;

    /** Represents a {@code Map} object that maps variables to nodes. */
    transient protected Map<Variable,Node> variablesToNode;

    /** Represents the probability of evidence. */
    protected double probOfEvidence = Double.NaN;

    /** Represents the initial seed. */
    protected long seed=0;

    /** Represents a {@link Random} object. */
    protected Random random = new Random(seed);

    /** Represents the maximum number of iterations. */
    protected int maxIter = 100;

    /** Represents a threshold. */
    protected double threshold = 0.000001;

    /** Represents the output. */
    protected boolean output = false;

    /** Represents the number of iterations. */
    protected int nIter = 0;

    /** Represents the evidence lower bound. */
    protected double local_elbo = -Double.MAX_VALUE;

    /** Represents the number of local iterations. */
    protected int local_iter = 0;

    /** Store weather parallel message passing will be employed or not.**/
    private boolean parallelMode = false;

    /**
     * Sets the output for this MessagePassingAlgorithm.
     * @param output a {@code boolean} that represents the output value to be set.
     */
    public void setOutput(boolean output) {
        this.output = output;
    }

    /**
     * Gets whether output for this MessagePassingAlgorithm.
     * @return
     */
    public boolean isOutput() {
        return output;
    }

    /**
     * Sets the threshold for this MessagePassingAlgorithm.
     * @param threshold a {@code double} that represents the threshold value to be set.
     */
    public void setThreshold(double threshold) {
        this.threshold = threshold;
    }

    /**
     * Returns the threshold of this MessagePassingAlgorithm.
     * @return the threshold of this MessagePassingAlgorithm.
     */
    public double getThreshold() {
        return threshold;
    }

    /**
     * Sets the maximum number of iterations for this MessagePassingAlgorithm.
     * @param maxIter a {@code int} that represents the  maximum number of iterations to be set.
     */
    public void setMaxIter(int maxIter) {
        this.maxIter = maxIter;
    }

    /**
     * Returns the maximum number of iterations of this MessagePassingAlgorithm.
     * @return the maximum number of iterations of this MessagePassingAlgorithm.
     */
    public int getMaxIter() {
        return maxIter;
    }

    public long getSeed() { return this.seed; }

    /**
     * Resets the exponential family distributions of all nodes.
     */
    public void resetQs(){
        this.nodes.stream().filter(node -> node.isActive()).forEach(node -> {node.resetQDist(random);});
    }

    public void resetQs(List<Node> nodes) {
        nodes.stream().filter(node -> node.isActive()).forEach(node -> {node.resetQDist(random);});
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setSeed(long seed) {
        this.seed=seed;
        random = new Random(seed);
    }

    /**
     * Sets the parallel processing mode.
     * @param parallelMode {@code true} if the learning is performed in parallel, {@code false} otherwise.
     */
    public void setParallelMode(boolean parallelMode){
        this.parallelMode = parallelMode;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void runInference() {

        this.nIter = 0;

        boolean convergence = false;
        this.local_elbo = Double.NEGATIVE_INFINITY;
        this.local_iter = 0;
        while (!convergence && (this.local_iter++) < this.maxIter) {

            boolean done = true;
            for (Node node : this.nodes) {


                if (!node.isActive() || node.isObserved())
                    continue;

                Message<E> selfMessage = newSelfMessage(node);

                Stream<Node> streamChildren = (this.parallelMode)? node.getChildren().parallelStream() : node.getChildren().stream();

                Optional<Message<E>> message = streamChildren
                                .filter(children -> children.isActive())
                                .map(children -> newMessageToParent(children, node))
                                .reduce(Message::combineNonStateless);

                if (message.isPresent())
                    selfMessage.combine(message.get());

                //for (Node child: node.getChildren()){
                //    selfMessage = Message.combine(newMessageToParent(child, node), selfMessage);
                //}

                updateCombinedMessage(node, selfMessage);
                done &= node.isDone();

            }

              convergence = this.testConvergence();

            if (done) {
                convergence = true;
            }

        }

        this.probOfEvidence = this.local_elbo;
        if (this.output){
            System.out.println("N Iter: "+this.local_iter +", elbo:"+this.local_elbo);
            logger.info("N Iter: {}, elbo: {}", this.local_iter, this.local_elbo);
        }
        this.nIter=this.local_iter;
    }

    /* ****************************************************************************************************************/

    /** Runs EM inference */
    public void emInference() { this.emInference(this.nodes, this.maxIter); }

    /** Runs inference on a list of nodes until convergence (or the maximmum number of iterations is reached) */
    public void emInference(List<Node> nodes) { this.emInference(nodes, this.maxIter); }

    /** Runs a inference for a number of iterations or until convergence. */
    public void emInference(int nIterations) { this.emInference(this.nodes, nIterations); }

    /** Runs a inference on a list of nodes for a number of iterations or until convergence. */
    public void emInference(List<Node> nodes, int nIterations) {
        this.nIter = 0;
        this.local_iter = 0;
        boolean convergence = false;
        this.local_elbo = Double.NEGATIVE_INFINITY;
        List<Node> latentNodes = new ArrayList<>();
        List<Node> parameterNodes = new ArrayList<>();

        for (Node node : nodes) {
            if (!node.isActive() || node.isObserved())
                continue;
            if(node.getQDist().getVariable().isParameterVariable())
                parameterNodes.add(node);
            else
                latentNodes.add(node);
        }

        while (!convergence && (this.local_iter++) < nIterations) {

            boolean done = true;

            /* Expectation step */
            for(Node node: latentNodes){
                nodeMessagePassing(node);
                done &= node.isDone();
            }

            /* Maximization step */
            for(Node node: parameterNodes) {
                nodeMessagePassing(node);
                done &= node.isDone();
            }

            convergence = this.testConvergence();

            if (done)
                convergence = true;
        }

        this.probOfEvidence = this.local_elbo;
        this.nIter=this.local_iter;
    }

    /** Runs a series of inference iterations, independently of convergence. */
    public void emInferenceWithoutConvergence(int nIterations) { this.emInferenceWithoutConvergence(this.nodes, nIterations); }

    /** Runs a series of inference iterations, independently of convergence. */
    public void emInferenceWithoutConvergence(List<Node> nodes, int nIterations) {

        this.nIter = 0;
        this.local_iter = 0;
        List<Node> latentNodes = new ArrayList<>();
        List<Node> parameterNodes = new ArrayList<>();

        for (Node node : nodes) {
            if (!node.isActive() || node.isObserved())
                continue;
            if(node.getQDist().getVariable().isParameterVariable())
                parameterNodes.add(node);
            else
                latentNodes.add(node);
        }

        while ((local_iter++) < nIterations) {

            /* Expectation step */
            for(Node node: latentNodes){
                nodeMessagePassing(node);
            }

            /* Maximization step */
            for(Node node: parameterNodes) {
                nodeMessagePassing(node);
            }
        }

        this.probOfEvidence = computeLogProbabilityOfEvidence();
        this.nIter = local_iter;
    }

    /* ****************************************************************************************************************/
    public void runInferenceHC(List<Node> nodes) {
        this.nIter = 0;

        boolean convergence = false;
        this.local_elbo = Double.NEGATIVE_INFINITY;
        this.local_iter = 0;
        while (!convergence && (this.local_iter++) < this.maxIter) {

            boolean done = true;
            for (Node node : nodes) {


                if (!node.isActive() || node.isObserved())
                    continue;

                Message<E> selfMessage = newSelfMessage(node);

                Stream<Node> streamChildren = (this.parallelMode)? node.getChildren().parallelStream() : node.getChildren().stream();

                Optional<Message<E>> message = streamChildren
                        .filter(children -> children.isActive())
                        .map(children -> newMessageToParent(children, node))
                        .reduce(Message::combineNonStateless);

                if (message.isPresent())
                    selfMessage.combine(message.get());

                //for (Node child: node.getChildren()){
                //    selfMessage = Message.combine(newMessageToParent(child, node), selfMessage);
                //}

                updateCombinedMessage(node, selfMessage);
                done &= node.isDone();

            }

            convergence = this.testConvergenceHC(nodes);

            if (done) {
                convergence = true;
            }

        }

        this.probOfEvidence = this.local_elbo;
        if (this.output){
            System.out.println("N Iter: "+this.local_iter +", elbo:"+this.local_elbo);
            logger.info("N Iter: {}, elbo: {}", this.local_iter, this.local_elbo);
        }
        this.nIter=this.local_iter;
    }

    public abstract boolean testConvergenceHC(List<Node> nodes);

    private void nodeMessagePassing(Node node) {

        Message<E> selfMessage = newSelfMessage(node);

        Stream<Node> streamChildren = (this.parallelMode)? node.getChildren().parallelStream() : node.getChildren().stream();

        Optional<Message<E>> message = streamChildren
                .filter(children -> children.isActive())
                .map(children -> newMessageToParent(children, node))
                .reduce(Message::combineNonStateless);

        if (message.isPresent())
            selfMessage.combine(message.get());

        updateCombinedMessage(node, selfMessage);
    }

    /**
     * Returns the number of iterations of this MessagePassingAlgorithm.
     * @return the number of iterations of this MessagePassingAlgorithm.
     */
    public int getNumberOfIterations(){
        return nIter;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setModel(BayesianNetwork model_) {
        model = model_;
        this.setEFModel(new EF_BayesianNetwork(this.model));
    }

    /**
     * Sets the {@link EF_BayesianNetwork} model for this MessagePassing Algorithm.
     * @param model the {@link EF_BayesianNetwork} model to be set.
     */
    public void setEFModel(EF_BayesianNetwork model){
        ef_model = model;

        variablesToNode = new ConcurrentHashMap<>();
        nodes = ef_model.getDistributionList()
                .stream()
                .map(dist -> {
                    Node node = new Node(dist);
                    variablesToNode.put(dist.getVariable(), node);
                    return node;
                })
                .collect(Collectors.toList());

        for (Node node : nodes){
            node.setParents(node.getPDist().getConditioningVariables().stream().map(this::getNodeOfVar).collect(Collectors.toList()));
            node.getPDist().getConditioningVariables().stream().forEach(var -> this.getNodeOfVar(var).getChildren().add(node));
        }
    }

    /**
     * Returns the {@link EF_BayesianNetwork} model.
     * @return the {@link EF_BayesianNetwork} model.
     */
    public EF_BayesianNetwork getEFModel() {
        return ef_model;
    }

    /**
     * Returns the {@link Node} associated with a given {@link Variable}.
     * @param variable a given {@link Variable} object
     * @return a {@link Node} object.
     */
    public Node getNodeOfVar(Variable variable){
        return this.variablesToNode.get(variable);
    }

    /**
     * Returns the list of nodes.
     * @return a {@code List} of {@link Node}s.
     */
    public List<Node> getNodes() {
        return nodes;
    }

    /**
     * Sets the list of nodes.
     * @param nodes a {@code List} of {@link Node}s to be set.
     */
    /* MyNote: Puede que aqui se este haciendo una copia innecesaria. Habria que comprobarlo */
    public void setNodes(List<Node> nodes) {
        this.nodes = nodes;
        variablesToNode = new ConcurrentHashMap();
        nodes.stream().forEach( node -> variablesToNode.put(node.getMainVariable(),node));
    }

    /**
     * Updates the set of children and parents for each node.
     */
    public void updateChildrenAndParents(){
        for (Node node : nodes){
            node.setParents(
                    node.getPDist()
                            .getConditioningVariables()
                            .stream()
                            .map(this::getNodeOfVar)
                            .collect(Collectors.toList())
            );

            node.getPDist().getConditioningVariables().stream()
                    .forEach(var -> this.getNodeOfVar(var).getChildren().add(node));
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public BayesianNetwork getOriginalModel() {
        return this.model;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setEvidence(Assignment assignment_) {
        this.assignment = assignment_;
        nodes.stream().forEach(node -> node.setAssignment(assignment));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <E extends UnivariateDistribution> E getPosterior(Variable var) {
        return this.getNodeOfVar(var).getQDist().toUnivariateDistribution();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public double getLogProbabilityOfEvidence() {
        return this.probOfEvidence;
    }

    /**
     * Its current use is simply to apply a score penalizer and then reassign.
     */
    public void setLogProbabilityOfEvidence(double logProbabilityOfEvidence) {
        this.probOfEvidence = logProbabilityOfEvidence;
    }

    /**
     * Returns the exponential family posterior of a given {@link Variable}.
     * @param <E> a class extending {@link EF_UnivariateDistribution}
     * @param var a {@link Variable} object.
     * @return an {@link EF_UnivariateDistribution} object.
     */
    public <E extends EF_UnivariateDistribution> E getEFPosterior(Variable var) {
        return (E)this.getNodeOfVar(var).getQDist();
    }

    /**
     * Creates a new self message for a given {@link Node}.
     * @param node a {@link Node} object.
     * @return a {@link Message} object.
     */
    public abstract Message<E> newSelfMessage(Node node);

    /**
     * Creates a new message from  a given child {@link Node} to its parent.
     * @param child a child {@link Node}.
     * @param parent a parent {@link Node}.
     * @return a {@link Message} object.
     */
    public abstract Message<E> newMessageToParent(Node child, Node parent);

    /**
     * Updates the combined message for a given {@link Node}.
     * @param node a {@link Node} object.
     * @param message a {@link Message} object.
     */
    public abstract void updateCombinedMessage(Node node, Message<E> message);

    /**
     * Tests if the convergence is reached or not.
     * @return {@code true} if the convergence is reached, {@code false} otherwise.
     */
    public abstract boolean testConvergence();

    /**
     * Returns the log probability of the evidence.
     * @return the log probability of the evidence
     */
    public abstract double computeLogProbabilityOfEvidence();

    public abstract double computeLogProbabilityOfEvidenceHC(List<Node> nodes);

    public abstract Map<Variable, Double> computeLogProbabilityOfEvidenceMap();

    public void setnIter(int nIter) {
        this.nIter = nIter;
    }

    public void setLocal_elbo(double local_elbo) {
        this.local_elbo = local_elbo;
    }

    public double getLocal_elbo() {
        return local_elbo;
    }

    public void setLocal_iter(int local_iter) {
        this.local_iter = local_iter;
    }

    public void setProbOfEvidence(double probOfEvidence) {
        this.probOfEvidence = probOfEvidence;
    }

    public int getnIter() {
        return nIter;
    }

    public int getLocal_iter() {
        return local_iter;
    }

    public boolean isParallelMode() {
        return parallelMode;
    }
}