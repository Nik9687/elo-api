package com.github.wakingrufus.elo.tech.jersey;

import lombok.extern.slf4j.Slf4j;
import org.glassfish.hk2.utilities.ClasspathDescriptorFileFinder;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
public class JerseyDescriptorFinder extends ClasspathDescriptorFileFinder {


    JerseyDescriptorFinder() {

    }

    @Override
    public List<String> getDescriptorFileInformation() {
        List<String> list = super.getDescriptorFileInformation();
        list.add("META-INF/hk2-locator/default");
        return list;
    }

    @Override
    public List<InputStream> findDescriptorFiles() throws IOException {
        //this section will read through all of the JARs for inhabitant files
        final Enumeration<URL> metaInfUrls = this.getClass().getClassLoader()
                .getResources("META-INF/hk2-locator/default");
        Collection<URL> urls = new ArrayList<>();
        urls.addAll(Collections.list(metaInfUrls));

        log.info("found " + urls.size() + " descriptor files");
        // open the descriptor files
        List<UrlMapResult> mapResults = urls
                .stream()
                .map(this::openStream)
                .collect(Collectors.toList());

        //return everything that had no errors - you might want to at least log the errors
        return mapResults.stream()
                .filter(x -> x.getIoException() == null)
                .map(UrlMapResult::getInputStream)
                .collect(Collectors.toList());
    }

    private UrlMapResult openStream(final URL url) {
        InputStream inputStream = null;
        IOException ioException = null;
        try {
            inputStream = url.openStream();
        } catch (final IOException e) {
            log.error("error reading mapping: " + url.toString(), e);
            ioException = e;
        }

        return new UrlMapResult(inputStream, ioException);
    }

    // private struct for saving output of Java 8 lambda
    private class UrlMapResult {
        final InputStream inputStream;
        final IOException ioException;

        public UrlMapResult(InputStream inputStreamIn, IOException ioExceptionIn) {
            inputStream = inputStreamIn;
            ioException = ioExceptionIn;
        }

        public InputStream getInputStream() {
            return inputStream;
        }

        public IOException getIoException() {
            return ioException;
        }
    }
}