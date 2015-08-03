package org.jboss.da.communication.aprox.model;

import org.jboss.da.communication.model.GAV;

import java.util.Set;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
public class GAVDependencyTree {

    @Getter
    @Setter
    private GAV gav;

    @Getter
    @Setter
    private Set<GAVDependencyTree> dependencyTree;

}
