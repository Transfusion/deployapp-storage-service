package io.github.transfusion.deployapp.utilities;

import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.Engine;
import org.graalvm.polyglot.Source;
import org.springframework.beans.factory.annotation.Value;

public class GraalPolyglot {
    // https://stackoverflow.com/questions/7392466/recursively-convert-hash-containing-non-utf-chars-to-utf
    static String monkeyPatchHash = "class Hash\n" +
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

    static Source[] sources = {
            Source.create("ruby", "Encoding.default_external = 'ISO-8859-1'"),
            Source.create("ruby", "require 'app-info'"),
            Source.create("ruby", "require 'json'"),
            Source.create("ruby", monkeyPatchHash)
    };

    public static Context newPolyglotContext(Engine engine) {
        Context ctx = Context.newBuilder().engine(engine).allowAllAccess(true).build();
        for (Source source : sources) ctx.eval(source);
        return ctx;
    }

    @Value("${custom_app.ruby_clean_sleep}")
    static int rubyCleanSleep;

    public static void cleanUpRubyContext(Context ctx) {
        ctx.eval("ruby", "GC.start");
        System.gc();
        System.runFinalization();
        try {
            Thread.sleep(rubyCleanSleep);
        } catch (InterruptedException ignored) {
        }
    }
}
