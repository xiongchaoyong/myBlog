import request from "@/utils/request";

export interface Tool{
    id: number
  name: string
  url: string
  description: string
  avatar: string
  cardColor: string
}

export const getTools= async()=>{
    try {
        const res=await request.get('/user/tool/getAll')
        if(res.data.code === 1){
            return res.data.data as Tool[]
        }else{
            throw new Error(res.data.msg || '获取工具列表失败')
        }
    }
    catch (error) {
        throw error
    }
}

export const add=async (tool:Tool)=>{
    try{
        const res=await request.post('/user/tool/add',tool)
        if(res.data.code!= 1)
        {
            throw new Error(res.data.msg || '添加工具失败')
        }
        return res.data.data
    }
    catch(error){
        console.log(error)
        throw error
    }
}