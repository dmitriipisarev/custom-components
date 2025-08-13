package com.github.dmitriipisarev;

import java.util.Set;

public interface ContextPropsProvider {
    Set<String> getDownstreamHeaders();
}
