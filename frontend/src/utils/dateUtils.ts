/**
 * 时间格式化工具函数
 */

/**
 * 将后端返回的时间数组转换为Date对象
 * @param timeArray 时间数组 [year, month, day, hour, minute]
 * @returns Date对象
 */
export const parseTimeArray = (timeArray: number[]): Date => {
  if (!Array.isArray(timeArray) || timeArray.length < 5) {
    return new Date()
  }

  const [year, month, day, hour, minute] = timeArray
  // 注意：月份需要减1，因为JavaScript的Date构造函数中月份是从0开始的
  return new Date(year, month - 1, day, hour, minute)
}

/**
 * 格式化日期为短格式（月-日）
 * @param timeArray 时间数组或字符串
 * @returns 格式化后的日期字符串
 */
export const formatShortDate = (timeArray: number[] | string): string => {
  let date: Date

  if (Array.isArray(timeArray)) {
    date = parseTimeArray(timeArray)
  } else {
    date = new Date(timeArray)
  }

  return date.toLocaleDateString('zh-CN', {
    month: 'short',
    day: 'numeric'
  })
}

/**
 * 格式化日期为完整格式（年-月-日 时:分）
 * @param timeArray 时间数组或字符串
 * @returns 格式化后的日期字符串
 */
export const formatFullDate = (timeArray: number[] | string): string => {
  let date: Date

  if (Array.isArray(timeArray)) {
    date = parseTimeArray(timeArray)
  } else {
    date = new Date(timeArray)
  }

  return date.toLocaleString('zh-CN', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit'
  })
}

/**
 * 格式化日期为本地字符串格式
 * @param timeArray 时间数组或字符串
 * @returns 格式化后的日期字符串
 */
export const formatLocalDate = (timeArray: number[] | string): string => {
  let date: Date

  if (Array.isArray(timeArray)) {
    date = parseTimeArray(timeArray)
  } else {
    date = new Date(timeArray)
  }

  return date.toLocaleString('zh-CN')
} 