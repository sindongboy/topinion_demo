package com.skplanet.nlp;

import javax.servlet.annotation.WebServlet;

import com.vaadin.annotations.Theme;
import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.annotations.Widgetset;
import com.vaadin.data.Property;
import com.vaadin.event.MouseEvents;
import com.vaadin.server.*;
import com.vaadin.shared.Position;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.ui.*;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.themes.ValoTheme;

import java.util.*;

/**
 *
 */
@Theme("mytheme")
@Widgetset("com.skplanet.nlp.MyAppWidgetset")
public class TopinionApplication extends UI {

    // resources
    private static final String DEFAULT_TOPIC = "1";
    public static final int KEYWORD_NUM = 10;
    Ebooks ebooks = new Ebooks();

    // title
    HorizontalLayout titleLayout = null;
    Label titleLabel = null;

    // topic selection 
    VerticalLayout selectionLayout = null;

    /*
    ComboBox selectionBox = null;
    */
    ComboBox keywordSearchBox = null;

    // topic slider
    Slider selectionSlider = null;

    // topic keywords
    HorizontalLayout topicKeywordLayout = null;
    Button[] topicKeywordField = new Button[KEYWORD_NUM];

    // item list
    VerticalLayout ebookBaseLayout = null;
    List<HorizontalLayout> ebookRowLayoutList = new ArrayList<HorizontalLayout>();
    List<Panel> ebookPanelList = new ArrayList<Panel>();

    @Override
    protected void init(VaadinRequest vaadinRequest) {

        // --------------------------------------- //
        // RESOURCES
        // --------------------------------------- //
        this.ebooks.load();

        // --------------------------------------- //
        // TITLE LAYOUT
        // --------------------------------------- //
        this.titleLabel = new Label("Topinion eBook Demo");
        this.titleLabel.addStyleName(ValoTheme.LABEL_H1);
        this.titleLayout = new HorizontalLayout(this.titleLabel);
        this.titleLayout.setComponentAlignment(this.titleLabel, Alignment.MIDDLE_CENTER);
        this.titleLayout.setMargin(new MarginInfo(true, false, true, false));

        /*
        // --------------------------------------- //
        // SELECTION LAYOUT
        // --------------------------------------- //
        this.selectionBox = new ComboBox();
        this.selectionBox.setNullSelectionAllowed(false);
        this.selectionBox.setImmediate(true);
        this.selectionBox.setWidth("400px");
        this.selectionBox.setInputPrompt("Select Topic Class ...");
        for (String topic : this.ebooks.getTopicClass()) {
            this.selectionBox.addItem(topic);
        }
        this.selectionBox.addListener(new Property.ValueChangeListener() {
            @Override
            public void valueChange(Property.ValueChangeEvent valueChangeEvent) {
                if (selectionBox.getValue() != null) {
                    String topic = selectionBox.getValue().toString();
                    selectionSlider.setValue(Double.parseDouble(topic));
                    drawTopicPanels(topic);
                }
            }
        });
        */
        // --------------------------------------- //
        // KEYWORD SEARCH LAYOUT
        // --------------------------------------- //

        this.keywordSearchBox = new ComboBox();
        this.keywordSearchBox.setNullSelectionAllowed(false);
        this.keywordSearchBox.setImmediate(true);
        this.keywordSearchBox.setWidth("400px");
        this.keywordSearchBox.setInputPrompt("Select Topic Class ...");
        for (String keyword : this.ebooks.getKeywordAll()) {
            this.keywordSearchBox.addItem(keyword);
        }
        this.keywordSearchBox.addListener(new Property.ValueChangeListener() {
            @Override
            public void valueChange(Property.ValueChangeEvent valueChangeEvent) {
                if (keywordSearchBox.getValue() != null) {
                    String keyword = keywordSearchBox.getValue().toString();
                    List<String> topics = ebooks.getTopicListByKeyword(keyword);
                    String bestTopic = ebooks.getMostProbableTopicByKeyword(keyword);

                    /*
                    Map<Ebook, Double> probAll = new HashMap<Ebook, Double>();
                    
                    // ebook list all
                    List<Ebook> ebookAll = new ArrayList<Ebook>();
                    // ebook prob list all 
                    List<Double> ebookProbAll = new ArrayList<Double>();
                    // keyword list all
                    List<String> keywordAll = new ArrayList<String>();
                    
                    for (String topic : topics) {
                        // add ebook list
                        List<Ebook> ebookList = ebooks.getEbookListByTopic(topic);
                        ebookAll.addAll(ebookList);
                        
                        // add probabilities 
                        List<Double> probList = ebooks.getEbookProbListByTopic(topic);
                        ebookProbAll.addAll(probList);
                        
                        // add keyword list
                        List<String> keywordList = ebooks.getTopicKeyword(topic);
                        keywordAll.addAll(keywordList);
                    }

                    if (ebookAll.size() != ebookProbAll.size()) {
                        System.out.println("[ERROR] ebookAll and ebookProbAll size mismatched");
                        return;
                    }

                    for (int i = 0; i < ebookAll.size(); i++) {
                        probAll.put(ebookAll.get(i), ebookProbAll.get(i));
                    }

                    probAll = MapUtil.sortByValue(probAll, MapUtil.SORT_DESCENDING);

                    Iterator iter = probAll.entrySet().iterator();

                    List<Ebook> sortedEbook = new ArrayList<Ebook>();
                    
                    while (iter.hasNext()) {
                        Map.Entry entry = (Map.Entry) iter.next();
                        sortedEbook.add((Ebook) entry.getKey());
                    }

                    drawTopicPanelsAll(sortedEbook, keywordAll);
                    */
                    drawTopicPanels(bestTopic);
                }
            }
        });

        // --------------------------------------- //
        // SELECTION SLIDER LAYOUT
        // --------------------------------------- //
        this.selectionSlider = new Slider();
        //this.selectionSlider.setCaption("Topic Selector");
        this.selectionSlider.setWidth("400px");
        this.selectionSlider.setMax(this.ebooks.getTopicClass().size());
        this.selectionSlider.setMin(1.0);
        this.selectionSlider.setResolution(0);
        this.selectionSlider.setImmediate(true);
        this.selectionSlider.addListener(new Property.ValueChangeListener() {
            @Override
            public void valueChange(Property.ValueChangeEvent valueChangeEvent) {
                String topic = ((int) Double.parseDouble(selectionSlider.getValue().toString())) + "";
                drawTopicPanels(topic);
            }
        });

        this.selectionLayout = new VerticalLayout(
                //this.selectionBox,
                this.keywordSearchBox,
                this.selectionSlider
        );
        this.selectionLayout.setSpacing(true);
        //this.selectionLayout.setComponentAlignment(this.selectionBox, Alignment.MIDDLE_CENTER);
        this.selectionLayout.setComponentAlignment(this.keywordSearchBox, Alignment.MIDDLE_CENTER);
        this.selectionLayout.setComponentAlignment(this.selectionSlider, Alignment.MIDDLE_CENTER);
        this.selectionLayout.setMargin(new MarginInfo(false, false, true, false));

        // --------------------------------------- //
        // TOPIC KEYWORD LAYOUT
        // --------------------------------------- //
        this.topicKeywordLayout = new HorizontalLayout();
        List<String> topicKeywords = this.ebooks.getTopicKeyword(DEFAULT_TOPIC);
        for (int i = 0; i < KEYWORD_NUM; i++) {
            this.topicKeywordField[i] = new Button(topicKeywords.get(i));
            this.topicKeywordField[i].addStyleName(ValoTheme.BUTTON_BORDERLESS);
            this.topicKeywordField[i].setEnabled(false);
            this.topicKeywordLayout.addComponent(this.topicKeywordField[i]);
            this.topicKeywordLayout.setComponentAlignment(this.topicKeywordField[i], Alignment.MIDDLE_CENTER);
        }
        this.topicKeywordLayout.setMargin(new MarginInfo(false, false, true, false));


        // --------------------------------------- //
        // ITEM DISPLAY LAYOUT
        // --------------------------------------- //
        this.ebookBaseLayout = new VerticalLayout();
        this.drawTopicPanels(DEFAULT_TOPIC);

        // --------------------------------------- //
        // Arrange Components
        // --------------------------------------- //
        final VerticalLayout layout = new VerticalLayout(
                this.titleLayout,
                this.selectionLayout,
                this.topicKeywordLayout,
                this.ebookBaseLayout
        );

        layout.setComponentAlignment(this.titleLayout, Alignment.MIDDLE_CENTER);
        layout.setComponentAlignment(this.selectionLayout, Alignment.MIDDLE_CENTER);
        layout.setComponentAlignment(this.topicKeywordLayout, Alignment.MIDDLE_CENTER);
        layout.setComponentAlignment(this.ebookBaseLayout, Alignment.MIDDLE_CENTER);
        setContent(layout);
    }

    private void drawTopicPanelsAll(final List<Ebook> ebookList, final List<String> topicKeywords) {
        /*
        final List<Ebook> ebookList = this.ebooks.getEbookListByTopic(topic);
        */
        this.ebookBaseLayout.removeAllComponents();
        this.ebookPanelList.clear();
        this.ebookRowLayoutList.clear();

        // create ebook row layout
        for (int i = 0; i < ebookList.size(); i++) {
            if (i % 5 == 0) {
                HorizontalLayout newRowLayout = new HorizontalLayout();
                this.ebookRowLayoutList.add(newRowLayout);
            }

            // set panels using ebook list
            Panel panel = new Panel();
            final int finalI = i;
            panel.addClickListener(new MouseEvents.ClickListener() {
                @Override
                public void click(MouseEvents.ClickEvent clickEvent) {
                    showInfo(ebookList.get(finalI).getId());
                }
            });
            panel.addStyleName(ValoTheme.PANEL_BORDERLESS);
            //Resource image = new ExternalResource("http://localhost:7878/" + ebookList.get(i).getId() + ".png");
            Resource image = new ThemeResource("img/" + ebookList.get(i).getId() + ".png");
            Image poster = new Image(null, image);
            panel.setContent(poster);
            if (ebookList.get(i).getTitle().length() > 15) {
                panel.setCaption(ebookList.get(i).getTitle().substring(0, 14));
            } else {
                panel.setCaption(ebookList.get(i).getTitle());
            }
            this.ebookPanelList.add(panel);
        }

        // put panels to each row layout
        for (int i = 0; i < this.ebookRowLayoutList.size(); i++) {
            for (int j = 0; j < 5 && i * 5 + j < this.ebookPanelList.size(); j++) {
                this.ebookRowLayoutList.get(i).addComponent(this.ebookPanelList.get(i * 5 + j));
                this.ebookRowLayoutList.get(i).setComponentAlignment(this.ebookPanelList.get(i * 5 + j), Alignment.MIDDLE_CENTER);
            }
            this.ebookRowLayoutList.get(i).setSpacing(true);
        }

        // update topic keyword
        /*
        List<String> topicKeywords = this.ebooks.getTopicKeyword(topic);
        */
        for (int i = 0; i < topicKeywords.size() && i < KEYWORD_NUM; i++) {
            this.topicKeywordField[i].setCaption(topicKeywords.get(i));
        }

        // arrange components
        for (HorizontalLayout rowLayout : this.ebookRowLayoutList) {
            this.ebookBaseLayout.addComponent(rowLayout);
            this.ebookBaseLayout.setComponentAlignment(rowLayout, Alignment.MIDDLE_CENTER);
        }
        this.ebookBaseLayout.setSpacing(true);
    }

    private void drawTopicPanels(String topic) {
        final List<Ebook> ebookList = this.ebooks.getEbookListByTopic(topic);
        this.ebookBaseLayout.removeAllComponents();
        this.ebookPanelList.clear();
        this.ebookRowLayoutList.clear();

        // create ebook row layout
        for (int i = 0; i < ebookList.size(); i++) {
            if (i % 5 == 0) {
                HorizontalLayout newRowLayout = new HorizontalLayout();
                this.ebookRowLayoutList.add(newRowLayout);
            }

            // set panels using ebook list
            Panel panel = new Panel();
            final int finalI = i;
            panel.addClickListener(new MouseEvents.ClickListener() {
                @Override
                public void click(MouseEvents.ClickEvent clickEvent) {
                    showInfo(ebookList.get(finalI).getId());
                }
            });
            panel.addStyleName(ValoTheme.PANEL_BORDERLESS);
            //Resource image = new ExternalResource("http://localhost:7878/" + ebookList.get(i).getId() + ".png");
            Resource image = new ThemeResource("img/" + ebookList.get(i).getId() + ".png");
            Image poster = new Image(null, image);
            panel.setContent(poster);
            if (ebookList.get(i).getTitle().length() > 15) {
                panel.setCaption(ebookList.get(i).getTitle().substring(0, 14));
            } else {
                panel.setCaption(ebookList.get(i).getTitle());
            }
            this.ebookPanelList.add(panel);
        }

        // put panels to each row layout
        for (int i = 0; i < this.ebookRowLayoutList.size(); i++) {
            for (int j = 0; j < 5 && i * 5 + j < this.ebookPanelList.size(); j++) {
                this.ebookRowLayoutList.get(i).addComponent(this.ebookPanelList.get(i * 5 + j));
                this.ebookRowLayoutList.get(i).setComponentAlignment(this.ebookPanelList.get(i * 5 + j), Alignment.MIDDLE_CENTER);
            }
            this.ebookRowLayoutList.get(i).setSpacing(true);
        }

        // update topic keyword
        List<String> topicKeywords = this.ebooks.getTopicKeyword(topic);
        for (int i = 0; i < topicKeywords.size() && i < KEYWORD_NUM; i++) {
            this.topicKeywordField[i].setCaption(topicKeywords.get(i));
        }

        // arrange components
        for (HorizontalLayout rowLayout : this.ebookRowLayoutList) {
            this.ebookBaseLayout.addComponent(rowLayout);
            this.ebookBaseLayout.setComponentAlignment(rowLayout, Alignment.MIDDLE_CENTER);
        }
        this.ebookBaseLayout.setSpacing(true);
    }

    /**
     * Show Movie Information
     * @param id ebook id
     */
    public void showInfo(String id) {
        Ebook ebook = this.ebooks.getEbookById(id);

        Notification notif;
        notif = new Notification(
                ebook.getTitle(),
                "<font color=\"red\">Genre: </font>" + ebook.getGenre() + "<br>" +
                        "<font color=\"red\">Author: </font>" + ebook.getAuthor() + "<br>" +
                        "<font color=\"red\">Publish: </font>" + ebook.getPublish() + "<br>" +
                        "<font color=\"red\">Date: </font>" + ebook.getDate() + "<br>" +
                        "<font color=\"red\">Story: </font>" + ebook.getSynopsis(), Notification.Type.HUMANIZED_MESSAGE);

        // Customize it
        notif.setHtmlContentAllowed(true);
        notif.setDelayMsec(20000);
        notif.setPosition(Position.MIDDLE_CENTER);
        //notif.setIcon(new ExternalResource("http://localhost:7878/" + id + ".png"));
        notif.setIcon(new ThemeResource("img/" + id + ".png"));

        // Show it in the page
        notif.show(Page.getCurrent());

    }


    @WebServlet(urlPatterns = "/*", name = "MyUIServlet", asyncSupported = true)
    @VaadinServletConfiguration(ui = TopinionApplication.class, productionMode = false)
    public static class MyUIServlet extends VaadinServlet {
    }
}
