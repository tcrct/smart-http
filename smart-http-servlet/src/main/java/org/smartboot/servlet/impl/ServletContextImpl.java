package org.smartboot.servlet.impl;

import org.smartboot.http.utils.Mimetypes;
import org.smartboot.servlet.DeploymentRuntime;
import org.smartboot.servlet.conf.DeploymentInfo;

import javax.servlet.Filter;
import javax.servlet.FilterRegistration;
import javax.servlet.RequestDispatcher;
import javax.servlet.Servlet;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRegistration;
import javax.servlet.SessionCookieConfig;
import javax.servlet.SessionTrackingMode;
import javax.servlet.descriptor.JspConfigDescriptor;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collections;
import java.util.Enumeration;
import java.util.EventListener;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * @author 三刀
 * @version V1.0 , 2019/12/11
 */
public class ServletContextImpl implements ServletContext {
    //    private static final Logger LOGGER = LoggerFactory.getLogger(ServletContextImpl.class);
    private final ConcurrentMap<String, Object> attributes = new ConcurrentHashMap<>();
    private DeploymentInfo deploymentInfo;
    private SessionCookieConfig sessionCookieConfig = new SessionCookieConfigImpl();

    public ServletContextImpl(DeploymentInfo deploymentInfo) {
        this.deploymentInfo = deploymentInfo;
    }

    @Override

    public String getContextPath() {
        String contextPath = deploymentInfo.getContextPath();
        if (contextPath.equals("/")) {
            return "";
        }
        return contextPath;
    }

    @Override
    public ServletContext getContext(String uripath) {
        //获取uri归属的 DeploymentRuntime
//        LOGGER.error("unSupport now");
        DeploymentRuntime runtime = null;
        if (runtime == null) {
            return null;
        }
        return runtime.getServletContext();
    }

    @Override
    public int getMajorVersion() {
        return 0;
    }

    @Override
    public int getMinorVersion() {
        return 0;
    }

    @Override
    public int getEffectiveMajorVersion() {
        return 0;
    }

    @Override
    public int getEffectiveMinorVersion() {
        return 0;
    }

    @Override
    public String getMimeType(String file) {
        return Mimetypes.getInstance().getMimetype(file);
    }

    @Override
    public Set<String> getResourcePaths(String path) {
        throw new UnsupportedOperationException();
    }

    @Override
    public URL getResource(String path) throws MalformedURLException {
        File file = new File(getRealPath(path));
        System.out.println(file.getAbsolutePath());
        return file.toURI().toURL();
    }

    @Override
    public InputStream getResourceAsStream(String path) {
        InputStream inputStream = null;
        try {
            inputStream = new FileInputStream(getRealPath(path));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return inputStream;
    }

    @Override
    public RequestDispatcher getRequestDispatcher(String path) {
        throw new UnsupportedOperationException();
    }

    @Override
    public RequestDispatcher getNamedDispatcher(String name) {
        System.out.println("getNamedDispatcher:" + name);
        return new RequestDispatcherImpl();
    }

    @Override
    public Servlet getServlet(String name) throws ServletException {
        throw new UnsupportedOperationException();
    }

    @Override
    public Enumeration<Servlet> getServlets() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Enumeration<String> getServletNames() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void log(String msg) {
//        LOGGER.info(msg);
    }

    @Override
    public void log(Exception exception, String msg) {
//        LOGGER.error(msg, exception);
    }

    @Override
    public void log(String message, Throwable throwable) {
//        LOGGER.error(message, throwable);
    }

    @Override
    public String getRealPath(String path) {
        if (path == null)
            return null;
        if (path.length() == 0)
            path = "/";
        else if (path.charAt(0) != '/')
            path = "/" + path;

        try {
            path = deploymentInfo.getRealPath() + path;
//            LOGGER.info("path:{}", path);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return path;
    }

    @Override
    public String getServerInfo() {
        return "smart-servlet";
    }

    @Override
    public String getInitParameter(String name) {
        String value = deploymentInfo.getInitParameters().get(name);
//        System.out.println("context param:" + name + " value:" + value);
        return value;
    }

    @Override
    public Enumeration<String> getInitParameterNames() {
        return Collections.enumeration(deploymentInfo.getInitParameters().keySet());
    }

    @Override
    public boolean setInitParameter(String name, String value) {
        if (deploymentInfo.getInitParameters().containsKey(name)) {
            return false;
        }
        deploymentInfo.addInitParameter(name, value);
        return true;
    }

    @Override
    public Object getAttribute(String name) {
        return attributes.get(name);
    }

    @Override
    public Enumeration<String> getAttributeNames() {
        return Collections.enumeration(attributes.keySet());
    }

    @Override
    public void setAttribute(String name, Object object) {
        if (object == null) {
            //todo 补充ServletContextAttributeListener#attributeRemoved
            Object existing = attributes.remove(name);
        } else {
            Object existing = attributes.put(name, object);
            //todo 补充ServletContextAttributeListener#attributeReplaced 或 attributeAdded
        }
//        throw new UnsupportedOperationException();
    }

    @Override
    public void removeAttribute(String name) {
        Object exiting = attributes.remove(name);
        //todo 补充ServletContextAttributeListener#attributeRemoved
        throw new UnsupportedOperationException();
    }

    @Override
    public String getServletContextName() {
        return deploymentInfo.getDisplayName();
    }

    @Override
    public ServletRegistration.Dynamic addServlet(String servletName, String className) {
        throw new UnsupportedOperationException();
    }

    @Override
    public ServletRegistration.Dynamic addServlet(String servletName, Servlet servlet) {
        throw new UnsupportedOperationException();
    }

    @Override
    public ServletRegistration.Dynamic addServlet(String servletName, Class<? extends Servlet> servletClass) {
        throw new UnsupportedOperationException();
    }

    @Override
    public <T extends Servlet> T createServlet(Class<T> clazz) throws ServletException {
        throw new UnsupportedOperationException();
    }

    @Override
    public ServletRegistration getServletRegistration(String servletName) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Map<String, ? extends ServletRegistration> getServletRegistrations() {
        throw new UnsupportedOperationException();
    }

    @Override
    public FilterRegistration.Dynamic addFilter(String filterName, String className) {
        throw new UnsupportedOperationException();
    }

    @Override
    public FilterRegistration.Dynamic addFilter(String filterName, Filter filter) {
        throw new UnsupportedOperationException();
    }

    @Override
    public FilterRegistration.Dynamic addFilter(String filterName, Class<? extends Filter> filterClass) {
        throw new UnsupportedOperationException();
    }

    @Override
    public <T extends Filter> T createFilter(Class<T> clazz) throws ServletException {
        throw new UnsupportedOperationException();
    }

    @Override
    public FilterRegistration getFilterRegistration(String filterName) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Map<String, ? extends FilterRegistration> getFilterRegistrations() {
        throw new UnsupportedOperationException();
    }

    @Override
    public SessionCookieConfig getSessionCookieConfig() {
        return sessionCookieConfig;
    }

    @Override
    public void setSessionTrackingModes(Set<SessionTrackingMode> sessionTrackingModes) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Set<SessionTrackingMode> getDefaultSessionTrackingModes() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Set<SessionTrackingMode> getEffectiveSessionTrackingModes() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void addListener(String className) {
        throw new UnsupportedOperationException();
    }

    @Override
    public <T extends EventListener> void addListener(T t) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void addListener(Class<? extends EventListener> listenerClass) {
        throw new UnsupportedOperationException();
    }

    @Override
    public <T extends EventListener> T createListener(Class<T> clazz) throws ServletException {
        throw new UnsupportedOperationException();
    }

    @Override
    public JspConfigDescriptor getJspConfigDescriptor() {
        throw new UnsupportedOperationException();
    }

    @Override
    public ClassLoader getClassLoader() {
        return deploymentInfo.getClassLoader();
    }

    @Override
    public void declareRoles(String... roleNames) {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getVirtualServerName() {
        throw new UnsupportedOperationException();
    }
}
