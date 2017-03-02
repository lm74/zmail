package com.zhy.smail.common.controller;

import com.zhy.smail.MainApp;
import com.zhy.smail.delivery.service.SqlSuggestionProvider;
import com.zhy.smail.user.entity.UserInfo;
import javafx.event.EventHandler;
import javafx.scene.control.TextField;
import org.controlsfx.control.textfield.AutoCompletionBinding;
import org.controlsfx.control.textfield.TextFields;

import java.util.List;

/**
 * Created by wenliz on 2017/2/21.
 */
public class RootController {
    protected MainApp app;

    public MainApp getApp() {
        return app;
    }

    public void setApp(MainApp app) {
        this.app = app;
    }

    protected void createUserAutoCombBox(TextField txtRoomNo){
        SqlSuggestionProvider provider = SqlSuggestionProvider.create();
        AutoCompletionBinding<String> binding = TextFields.bindAutoCompletion(txtRoomNo, provider);
        binding.setVisibleRowCount(50);
        binding.setMinWidth(800);
        binding.setOnAutoCompleted(new EventHandler<AutoCompletionBinding.AutoCompletionEvent<String>>() {
            @Override
            public void handle(AutoCompletionBinding.AutoCompletionEvent<String> event) {
                List suggestions = provider.getActualSuggestions();
                int index = -1;
                for(int i=0; i<suggestions.size(); i++){
                    String suggest = (String)suggestions.get(i);
                    if(suggest.equals(event.getCompletion())){
                        index = i;
                    }
                }
                if(index>-1){
                    UserInfo user = (UserInfo)provider.getProviders().get(index);
                    changeUser(user);
                    txtRoomNo.setText(user.getUserName());
                }
                else{
                    changeUser(null);
                    txtRoomNo.setText("");
                }
            }
        });
    }

    protected void  changeUser(UserInfo user){

    }
}
