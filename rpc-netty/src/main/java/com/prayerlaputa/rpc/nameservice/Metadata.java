package com.prayerlaputa.rpc.nameservice;

import java.net.URI;
import java.util.HashMap;
import java.util.List;

/**
 * @author chenglong.yu
 * created on 2020/9/4
 */
public class Metadata extends HashMap<String, List<URI>> {

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder("Metadata:");
        builder.append("\n");
        for (Entry<String, List<URI>> entry : entrySet()) {
            builder.append("\t")
                    .append("ClassName: ")
                    .append(entry.getKey())
                    .append("\n")
                    .append("\t")
                    .append("URIs:")
                    .append("\n");

            for (URI uri : entry.getValue()) {
                builder.append("\t\t")
                        .append(uri)
                        .append("\n");
            }
        }
        return builder.toString();
    }
}
