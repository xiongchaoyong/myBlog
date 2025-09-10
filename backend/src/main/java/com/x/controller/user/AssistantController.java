package com.x.controller.user;

import com.x.ai.api.Assistant;
import com.x.ai.api.TitleAssistant;
import com.x.context.BaseContext;
import com.x.pojo.dto.UpdateSessionTitleDTO;
import com.x.pojo.entity.ChatMessage;
import com.x.pojo.entity.Session;
import com.x.service.ChatMessageService;
import com.x.service.SessionService;
import com.x.utils.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

import java.util.List;

@RestController
@RequestMapping("/ai")
@CrossOrigin
class AssistantController {

    @Autowired
    private Assistant assistant;
    @Autowired
    private TitleAssistant titleAssistant;
    @Autowired
    private ChatMessageService chatMessageService;
    @Autowired
    private SessionService sessionService;
    @GetMapping(value = "/chat", produces = "text/stream;charset=UTF-8")
    public Flux<String> chat(@RequestParam("message") String message) {

        return assistant.chat(BaseContext.getCurUserInfo().getId(),message);
    }
    @GetMapping("/getCurMsgList/{sessionId}")
    public Result<List<ChatMessage>> getCurMsgList(@PathVariable Long sessionId){
        List<ChatMessage> list = chatMessageService.getCurMsgList(sessionId);
        return Result.success(list);
    }

    @GetMapping("/getSessionList")
    public Result<List<Session>> getSessionList(){
        List<Session> list = sessionService.getSessionList(BaseContext.getCurUserInfo().getId());
        return Result.success(list);
    }

    @PostMapping("/addMsg")
    public Result<Long> addMsg(@RequestBody ChatMessage chatMessage){
       Long id= chatMessageService.addMsg(chatMessage);
        return Result.success(id);
    }

    @PostMapping("/addSession")
    public Result<Long> addSession(@RequestBody Session session){
      Long id =  sessionService.addSession(session);
        return Result.success(id);
    }


    @DeleteMapping("/deleteMsg/{msgId}")

    public Result deleteMsg(@PathVariable Long msgId){
        chatMessageService.deleteMsg(msgId);
        return Result.success();
    }

    @PostMapping("/updateSessionTitle")
    public  Result<UpdateSessionTitleDTO> updateSessionTitle(@RequestBody UpdateSessionTitleDTO updateSessionTitleDTO){
        updateSessionTitleDTO.setTitle(titleAssistant.chat(updateSessionTitleDTO.getTitle()));
        sessionService.updateSessionTitle(updateSessionTitleDTO);
        return Result.success(updateSessionTitleDTO);
    }
    @DeleteMapping("/deleteSession/{sessionId}")
    public Result deleteSession(@PathVariable Long sessionId){
        sessionService.deleteSession(sessionId);
        return Result.success();
    }
}