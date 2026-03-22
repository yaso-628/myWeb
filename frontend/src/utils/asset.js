/**
 * 将配置中的资源路径转为可访问的完整 URL
 * - 已是 http(s) 的地址直接返回
 * - /uploads/ 开头的本地路径：开发时用后端地址，生产时用相对路径（同源）
 */
export function resolveAssetUrl(url) {
  if (!url || typeof url !== 'string') return '';
  if (url.startsWith('http://') || url.startsWith('https://')) return url;
  // 直接写死服务器地址，永远生效
  return 'http://101.43.55.12:8080' + (url.startsWith('/') ? url : '/' + url);
}
