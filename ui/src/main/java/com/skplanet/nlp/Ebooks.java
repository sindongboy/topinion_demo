package com.skplanet.nlp;

import com.vaadin.server.VaadinService;

import java.io.*;
import java.util.*;

/**
 * @author Donghun Shin / donghun.shin@sk.com
 * @date 1/23/15
 */
public class Ebooks {
    private static final String META_FILE_NAME = "/VAADIN/themes/mytheme/data/ebook.meta";
    private static final String KEYWORD_MAP_FILE_NAME = "/VAADIN/themes/mytheme/data/keyword.map";
    private static final String TOPIC_MAP_FILE_NAME = "/VAADIN/themes/mytheme/data/topic.map";

    private Map<String, Ebook> ebookMapId = null;
    private Map<String, Ebook> ebookMapName = null;
    private Map<String, String> keywordMap = null;
    private Map<String, String> topicMap = null;
    private Map<String, String> keyword2TopicMap = null;
    private List<String> idList = null;

    public Ebooks() {
        this.ebookMapId = new HashMap<String, Ebook>();
        this.ebookMapName = new HashMap<String, Ebook>();
        this.idList = new ArrayList<String>();

        this.keywordMap = new HashMap<String, String>();
        this.topicMap = new HashMap<String, String>();
        this.keyword2TopicMap = new HashMap<String, String>();
    }

    public void load() {
        String baseDirectory = VaadinService.getCurrent().getBaseDirectory().getAbsolutePath();
        String line;
        BufferedReader reader;

        File metaFile = new File(baseDirectory + META_FILE_NAME);
        File keywordMapFile = new File(baseDirectory + KEYWORD_MAP_FILE_NAME);
        File topicMapFile = new File(baseDirectory + TOPIC_MAP_FILE_NAME);

        try {
            int count = 0;
            // meta loading
            reader = new BufferedReader(new FileReader(metaFile));
            while ((line = reader.readLine()) != null) {
                if (line.trim().length() == 0) {
                    continue;
                }

                if (count % 1000 == 0) {
                    System.out.println("meta loading : " + count);
                }
                count++;

                String[] fields = line.split("\\t");
                
                /*
                0 : id
                1 : title
                2 : author
                3 : publish
                4 : genre
                5 : rate
                6 : date
                7 : synopsis
                 */

                if (fields.length != 8) {
                    System.out.println("field num error: " + line);
                }
                Ebook ebook = new Ebook();
                ebook.setId(fields[0].trim());
                ebook.setTitle(fields[1].trim());
                ebook.setAuthor(fields[2].trim());
                ebook.setPublish(fields[3].trim());
                ebook.setGenre(fields[4].trim());
                ebook.setRate(fields[5].trim());
                ebook.setDate(fields[6].trim());
                ebook.setSynopsis(fields[7].trim());

                this.ebookMapId.put(fields[0], ebook);
                this.ebookMapName.put(fields[1], ebook);
                this.idList.add(fields[0].trim());
            }
            reader.close();

            // keyword map
            count = 0;
            reader = new BufferedReader(new FileReader(keywordMapFile));
            while ((line = reader.readLine()) != null) {
                if (line.trim().length() == 0) {
                    continue;
                }
                System.out.println("keyword map loading : " + count);
                count++;

                String[] fields = line.split("\\t");
                /*
                0 : topic
                1 : keyword list
                 */

                // topic to keyword list
                this.keywordMap.put(fields[0], fields[1]);
                // keyword to topics
                for (String keyword : fields[1].split(" ")) {
                    if (this.keyword2TopicMap.containsKey(keyword)) {
                        String topicList = this.keyword2TopicMap.get(keyword);
                        topicList = topicList + "^" + fields[0];
                        this.keyword2TopicMap.remove(keyword);
                        this.keyword2TopicMap.put(keyword, topicList);
                    } else {
                        this.keyword2TopicMap.put(keyword, fields[0]);
                    }
                }
            }
            reader.close();

            // topic map
            count = 0;
            reader = new BufferedReader(new FileReader(topicMapFile));
            while ((line = reader.readLine()) != null) {
                if (line.trim().length() == 0) {
                    continue;
                }
                if (count % 1000 == 0) {
                    System.out.println("topic map loading : " + count);
                }
                count++;

                String[] fields = line.split("\\t");
                /*
                0 : id 
                1 : topic ( topic : prob )
                 */
                String topic = fields[1].split(" ")[0];
                this.topicMap.put(fields[0], topic);
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Ebook getEbookById(String id) {
        return this.ebookMapId.get(id);
    }

    public Ebook getEbookByName(String name) {
        return this.ebookMapName.get(name);
    }

    public String getTopicById(String id) {
        return this.topicMap.get(id).split(":")[0];
    }

    public String getTopicProbById(String id) {
        return this.topicMap.get(id).split(":")[1];
    }

    public List<Ebook> getEbookListByTopic(String topic) {
        List<Ebook> result = new ArrayList<Ebook>();
        for (String id : this.idList) {
            if (getTopicById(id).equals(topic)) {
                result.add(getEbookById(id));
            }
        }
        return result;
    }

    public List<Double> getEbookProbListByTopic(String topic) {
        List<Double> result = new ArrayList<Double>();
        for (String id : this.idList) {
            if (getTopicById(id).equals(topic)) {
                result.add(Double.parseDouble(getTopicProbById(id)));
            }
        }
        return result;
    }

    public List<String> getTopicKeyword(String topic) {
        List<String> result = new ArrayList<String>();
        for (String keyword : this.keywordMap.get(topic).split(" ")) {
            result.add(keyword);
        }

        if (result.size() > TopinionApplication.KEYWORD_NUM) {
            return result.subList(0, TopinionApplication.KEYWORD_NUM);
        }
        return result;
    }

    public List<String> getTopicClass() {
        List<String> result = new ArrayList<String>();
        for (String topic : this.keywordMap.keySet()) {
            result.add(topic);
        }
        return result;
    }

    /**
     * Get topic list from keyword
     * @param keyword keyword
     * @return list of topics
     */
    public List<String> getTopicListByKeyword(String keyword) {
        List<String> result = new ArrayList<String>();
        Collections.addAll(result, this.keyword2TopicMap.get(keyword).split("\\^"));
        return result;
    }

    /**
     * Get Keyword Set
     * @return keyword set
     */
    public Set<String> getKeywordAll() {
        return this.keyword2TopicMap.keySet();
    }

    public String getMostProbableTopicByKeyword(String keyword) {
        List<String> topicList = getTopicListByKeyword(keyword);
        String result = topicList.get(0);
        
        for (int i = 1; i < topicList.size(); i++) {
            int bestTopic = Integer.parseInt(result);
            List<String> bestKeywordList = getTopicKeyword(result);

            if (bestKeywordList.indexOf(keyword) < 0) {
                result = topicList.get(i);
                continue;
            }

            int currentTopic = Integer.parseInt(topicList.get(i));
            List<String> currentKeywordList = getTopicKeyword(topicList.get(i));

            if (currentKeywordList.indexOf(keyword) < 0) {
                continue;
            }

            if (bestKeywordList.indexOf(keyword) > currentKeywordList.indexOf(keyword)) {
                result = topicList.get(i);
            }
        }

        return result;
    }
}
