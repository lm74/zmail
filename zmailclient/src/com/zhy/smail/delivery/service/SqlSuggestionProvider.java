package com.zhy.smail.delivery.service;

import com.sun.jna.platform.win32.Netapi32Util;
import com.zhy.smail.lcp.LcResult;
import com.zhy.smail.restful.RestfulResult;
import com.zhy.smail.restful.RfFaultEvent;
import com.zhy.smail.restful.RfResultEvent;
import com.zhy.smail.user.entity.UserInfo;
import com.zhy.smail.user.service.UserService;
import impl.org.controlsfx.autocompletion.SuggestionProvider;
import javafx.util.Callback;
import org.controlsfx.control.textfield.AutoCompletionBinding;

import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * Created by wenliz on 2017/2/17.
 */
public class SqlSuggestionProvider<T>   implements Callback<AutoCompletionBinding.ISuggestionRequest, Collection<T>> {
    private static BlockingQueue<Integer> response = new LinkedBlockingQueue<Integer>();

    private final List<T> possibleSuggestions = new ArrayList();
    private final Object possibleSuggestionsLock = new Object();
    private List providers  ;
    private List actualSuggestions;

    public List getActualSuggestions() {
        return actualSuggestions;
    }

    public void setActualSuggestions(List actualSuggestions) {
        this.actualSuggestions = actualSuggestions;
    }

    public List getProviders() {
        return providers;
    }

    public void setProviders(List providers) {
        this.providers = providers;
    }

    public SqlSuggestionProvider() {
    }

    public void addPossibleSuggestions(T... newPossible) {
        this.addPossibleSuggestions((Collection)Arrays.asList(newPossible));
    }

    public void addPossibleSuggestions(Collection<T> newPossible) {
        Object var2 = this.possibleSuggestionsLock;
        synchronized(this.possibleSuggestionsLock) {
            this.possibleSuggestions.addAll(newPossible);
        }
    }

    public void clearSuggestions() {
        Object var1 = this.possibleSuggestionsLock;
        synchronized(this.possibleSuggestionsLock) {
            this.possibleSuggestions.clear();
        }
    }

    public final Collection<T> call(AutoCompletionBinding.ISuggestionRequest request) {
        ArrayList suggestions = new ArrayList();
        if(!request.getUserText().isEmpty()) {
            Object var3 = this.possibleSuggestionsLock;
            synchronized(this.possibleSuggestionsLock) {
                /*Iterator var4 = this.possibleSuggestions.iterator();

                while(true) {
                    if(!var4.hasNext()) {
                        break;
                    }

                    Object possibleSuggestion = var4.next();
                    if(this.isMatch(possibleSuggestion, request)) {
                        suggestions.add(possibleSuggestion);
                    }
                }*/
                UserService.listOwner(request.getUserText(), new RestfulResult() {
                    @Override
                    public void doResult(RfResultEvent event) {
                        if(event.getData()!=null){
                            List<UserInfo> users = (List<UserInfo>)event.getData();
                            providers = users;
                            List<String> emptys = new ArrayList<String>();
                            for(int i=0; i<users.size(); i++){
                                UserInfo user = users.get(i);
                                String item = user.getUserName() + "(";
                                boolean hasPrevious = false;
                                if(user.getBuildingNo()!=null && user.getBuildingNo().length()>0){
                                    item += "栋:" + user.getBuildingNo();
                                    hasPrevious = true;
                                }
                                if(user.getUnitNo() != null && user.getUnitNo().length()>0){
                                    if(hasPrevious){
                                        item+=",";
                                    }
                                    item += "单元:" + user.getUnitNo();
                                }
                                if(user.getFloorNo() != null && user.getFloorNo().length()>0){
                                    if(hasPrevious){
                                        item+=",";
                                    }
                                    item += "楼层:" + user.getFloorNo();
                                }
                                if(user.getRoomNo() != null && user.getRoomNo().length()>0){
                                    if(hasPrevious){
                                        item+=",";
                                    }
                                    item += "房号:" + user.getRoomNo();
                                }
                                item += ")  " ;
                                if(user.getPhoneNo()!= null && user.getPhoneNo().length()>0){
                                    item += user.getPhoneNo();
                                }
                                suggestions.add(item);
                                emptys.add(" ");
                            }
                            actualSuggestions = suggestions;
                            suggestions.addAll(emptys);
                        }
                        response.add(1);
                    }

                    @Override
                    public void doFault(RfFaultEvent event) {
                        response.add(1);
                    }
                });

            }
            try {
                response.poll(3, TimeUnit.SECONDS);
            }
            catch (Exception e){

            }

            //Collections.sort(suggestions, this.getComparator());
        }


        return suggestions;
    }
    private Callback<T, String> stringConverter;
    private final Comparator<T> stringComparator = new Comparator<T>() {
        public int compare(T o1, T o2) {
            String o1str = (String)SqlSuggestionProvider.this.stringConverter.call(o1);
            String o2str = (String)SqlSuggestionProvider.this.stringConverter.call(o2);
            return o1str.compareTo(o2str);
        }
    };

    public SqlSuggestionProvider(Callback<T, String> stringConverter) {
        this.stringConverter = stringConverter;
        if(this.stringConverter == null) {
            this.stringConverter = new Callback<T, String>() {
                public String call(T obj) {
                    return obj != null?obj.toString():"";
                }
            };
        }

    }


    protected Comparator<T> getComparator() {
        return this.stringComparator;
    }

    protected boolean isMatch(Object suggestion, AutoCompletionBinding.ISuggestionRequest request) {
        String userTextLower = request.getUserText().toLowerCase();
        String suggestionStr = suggestion.toString().toLowerCase();
        return suggestionStr.contains(userTextLower);
    }

    public static <T> SqlSuggestionProvider<T> create() {
        Collection possibleSuggestions = Arrays.asList(new String[]{});
        SqlSuggestionProvider suggestionProvider = new SqlSuggestionProvider(null);
        suggestionProvider.addPossibleSuggestions(possibleSuggestions);
        return suggestionProvider;
    }
}
