function formatDate(timestamp, format = 'date') {
  if (!timestamp) return '';

  const date = new Date(timestamp);

  const Y = date.getFullYear();
  const M = String(date.getMonth() + 1).padStart(2, '0');
  const D = String(date.getDate()).padStart(2, '0');
  const h = String(date.getHours()).padStart(2, '0');
  const m = String(date.getMinutes()).padStart(2, '0');
  const s = String(date.getSeconds()).padStart(2, '0');

  switch (format) {
    case 'time':
      return `${h}:${m}:${s}`; // 只显示时分秒
    case 'datetime':
      return `${Y}-${M}-${D} ${h}:${m}`; // 显示年月日时分
    case 'date':
      return `${Y}-${M}-${D}`; // 只显示年月日
    case 'all':
      return `${Y}-${M}-${D} ${h}:${m}:${s}`; // 显示年月日时分秒
    default:
      return `${Y}-${M}-${D}`;
  }
};

export default formatDate;
