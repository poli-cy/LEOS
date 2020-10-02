/*
 * Copyright 2019 European Commission
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at:
 *
 *     https://joinup.ec.europa.eu/software/page/eupl
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the Licence is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Licence for the specific language governing permissions and limitations under the Licence.
 */
package eu.europa.ec.leos.vo.toc;

import java.util.ArrayList;
import java.util.List;

public class TableOfContentItemHtmlVO {
    
    private String name;
    private String href;
    private List<TableOfContentItemHtmlVO> children = new ArrayList<>();
    
    public TableOfContentItemHtmlVO(String name) {
        this.name = name;
    }
    
    public TableOfContentItemHtmlVO(String name, String href) {
        this.name = name;
        this.href = href;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getHref() {
        return href;
    }
    
    public void setHref(String href) {
        this.href = href;
    }
    
    public List<TableOfContentItemHtmlVO> getChildren() {
        return children;
    }
    
    public void setChildren(List<TableOfContentItemHtmlVO> children) {
        this.children = children;
    }
    
    @Override
    public String toString() {
        return "TableOfContentItemHtmlVO [name=" + name + ", href=" + href + ", children=" + children + "]";
    }
}
