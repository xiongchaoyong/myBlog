import { defineStore } from "pinia";
import { computed, nextTick, ref } from "vue";
import type { ChatMessage } from "@/api/chatMessage";
import session from "@/api/session";
import type { Session } from "@/api/session";
import chatMessage from "@/api/chatMessage";
import { ElNotification } from "element-plus";
export const useAIChatStore = defineStore("aiChat", () => {
  //status
  const sessionList = ref<Session[]>([]);
  const allMesList = ref<Record<number, ChatMessage[]>>({});
  const curMesList = ref<ChatMessage[]>([]);
  const curSession = ref<Session>();

  //set
  const setSessionList = (list: Session[]) => {
    sessionList.value = list;
  };
  const setCurSession = (index: number) => {
    curSession.value = sessionList.value[index];
  };
  const setCurMesList = (list: ChatMessage[]) => {
    curMesList.value = list;
  };

  //get
  const getSessionList = computed(() => {
    return sessionList.value;
  });

  const getAllMesList = computed(() => {
    return allMesList.value;
  });

  const getCurSession = computed(() => {
    return curSession.value;
  });
  const getCurMesList = computed(() => {
    return curMesList.value || allMesList.value[getCurSession.value?.id as number];
  });

  //action
  const addSession = (session: Session) => {
    sessionList.value.push(session);
  };
  const addSessionDto = async (ses: Session) => {
    const res = await session.addSession(ses);
    if (res.data.code === 1) {
      return res.data.data;
    }
  };

  const addMesDto = async (mes: ChatMessage) => {
    const res = await chatMessage.addMsg(mes);
    if (res.data.code === 1) {
      return res.data.data;
    }
  };

  const fetchSessionList = async () => {
    if (getSessionList.value?.length > 0) {
      return;
    }
    const res = await session.getSessionList();
    if (res.data.code === 1) {
      setSessionList(res.data.data);
    }
  };

  const switchSession = async (index:number) => {
    if (allMesList.value[getCurSession.value?.id as number]) {
      setCurMesList(allMesList.value[getCurSession.value?.id as number]);
      return;
    }
    //优化,用allMesList存储所有消息,避免重复请求

    if (getCurSession.value) {
      getCurSession.value.isWaiting = true;
    }
    const res = await chatMessage.getCurMsgList(
      getCurSession.value?.id as number
    );
    if (res.data.code === 1) {
      //得到这个请求对应的会话id
      const curReqSessionId = sessionList.value[index]?.id
      allMesList.value[curReqSessionId as number] = res.data.data;
      //如果当前会话id是index的会话id，就set，负责不set
      if(curReqSessionId === getCurSession.value?.id){
        setCurMesList(res.data.data);
      }else{
        sessionList.value[index].isWaiting = false;
      }
      if (getCurSession.value) {
        getCurSession.value.isWaiting = false;
      }
    }
  };

  //删除session
  const deleteSession = async (id: number, curId: number) => {
    //保留旧的
    const oldSessionList = [...getSessionList.value];
    //得到删除的会话对象
    const delSession = oldSessionList.find((item) => item.id === id);
    sessionList.value = sessionList.value.filter((item) => item.id !== id);

    if (sessionList.value.length === 0) curSession.value = undefined;
    if (id === curId && sessionList.value.length > 0) {
      setCurSession(sessionList.value.length - 1);
      await nextTick(); // 确保 dom 更新
      switchSession(sessionList.value.length - 1);
    }

    //删除操作
    try {
      const res = await session.deleteSession(id);
      if (res.data.code !== 1) {
        ElNotification.error({
          title: "失败",
          message: `删除会话:"${delSession?.title}"失败`,
          type: "error",
        });
        //回滚
        sessionList.value = oldSessionList;
        return;
      } else {
        ElNotification.success({
          title: "成功",
          message: `已删除会话:"${delSession?.title}"`,
          type: "success",
        });
      }
    } catch (err: any) {
      ElNotification.error({
        title: "失败",
        message: `删除会话: "${delSession?.title}" 失败（${
          err?.message || "网络异常"
        })`,
        type: "error",
      });
      //回滚
      sessionList.value = oldSessionList;
    }
  };

  const clear = () => {
    sessionList.value = [];
    curSession.value = undefined;
    allMesList.value = {};
  };

  return {
    getSessionList,
    addSession,
    fetchSessionList,
    switchSession,
    addSessionDto,
    addMesDto,
    getCurSession,
    deleteSession,
    setCurSession,
    clear,
    getAllMesList,
    getCurMesList,
    setCurMesList,
  };
});
