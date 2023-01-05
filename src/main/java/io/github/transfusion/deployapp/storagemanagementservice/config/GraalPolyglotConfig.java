package io.github.transfusion.deployapp.storagemanagementservice.config;

import org.graalvm.polyglot.Context;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

@Configuration
public class GraalPolyglotConfig {

    // TODO: stress test this for memory leaks...
    @Bean
    @Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
    Context polyglotContext() {
        // https://stackoverflow.com/questions/7392466/recursively-convert-hash-containing-non-utf-chars-to-utf
        String monkeyPatchHash = "class Hash\n" +
                "  def to_utf8\n" +
                "    Hash[\n" +
                "      self.collect do |k, v|\n" +
                "        if (v.respond_to?(:to_utf8))\n" +
                "          [ k, v.to_utf8 ]\n" +
                "        elsif (v.respond_to?(:force_encoding))\n" +
                "          [ k, v.dup.force_encoding('UTF-8') ]\n" +
                "        else\n" +
                "          [ k, v ]\n" +
                "        end\n" +
                "      end\n" +
                "    ]\n" +
                "  end\n" +
                "end";

        Context ctx = Context.newBuilder().allowAllAccess(true).build();
        ctx.eval("ruby", "Encoding.default_external = 'ISO-8859-1'");
        ctx.eval("ruby", "require 'app-info'");
        ctx.eval("ruby", "require 'json'");
        ctx.eval("ruby", monkeyPatchHash);
        return ctx;
    }

}
