<template>
  <div class="starry-background">
    <!-- 粒子效果容器 -->
    <div id="tsparticles" ref="particlesContainer"></div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, onUnmounted } from 'vue'

const particlesContainer = ref<HTMLElement | null>(null)
let particlesInstance: any = null

/**
 * 初始化粒子效果
 */
const initParticles = async () => {
  // 动态加载 tsParticles 库
  if (typeof (window as any).tsParticles === 'undefined') {
    await loadTsParticlesScript()
  }

  const tsParticles = (window as any).tsParticles
  if (tsParticles && particlesContainer.value) {
    try {
      particlesInstance = await tsParticles.load("tsparticles", {
        fpsLimit: 60, // 限制最大帧率以优化性能
        // 粒子配置
        particles: {
          number: { value: 20, density: { enable: true, area: 800 } }, // 粒子数量和密度
          color: { value: ["#FFFFFF", "#ADD8E6", "#F0F8FF"] }, // 粒子颜色
          shape: { type: ["circle", "star"] }, // 粒子形状 (圆形和星形)
          opacity: {
            value: { min: 0.1, max: 0.4 }, // 随机透明度
            animation: { enable: true, speed: 1, minimumValue: 0.1, sync: false } // 透明度动画
          },
          size: { value: { min: 1, max: 3 } }, // 随机大小
          links: {
            color: "#ffffff",
            distance: 150,
            enable: true,
            opacity: 0.15,
            width: 1
          }, // 粒子间的连接线
          collisions: { enable: false }, // 禁用粒子碰撞
          move: {
            direction: "none",
            enable: true,
            outModes: { default: "out" }, // 移出画布的行为
            random: true,
            speed: 0.4, // 移动速度
            straight: false
          } // 移动行为
        },
        // 交互配置
        interactivity: {
          events: {
            onHover: { enable: true, mode: "repulse" }, // 鼠标悬停时排斥粒子
            onClick: { enable: true, mode: "push" }, // 鼠标点击时推出粒子
            resize: true // 窗口大小改变时自适应
          },
          modes: {
            repulse: { distance: 80, duration: 0.4 }, // 排斥效果的距离和持续时间
            push: { quantity: 3 } // 推出粒子的数量
          }
        },
        // 发射器配置 (创建流星效果)
        emitters: {
          direction: "none",
          rate: { quantity: 1, delay: 0.05 }, // 发射频率
          size: { width: 100, height: 100, mode: "percent" },
          position: { x: 50, y: 50 }, // 发射器位置
          particles: {
            shape: { type: "star" }, // 发射的粒子形状为星形
            size: { value: { min: 1, max: 3 } },
            color: { value: ["#FFFFFF", "#86efac", "#67e8f9", "#a7c7e7"] },
            opacity: {
              value: { min: 0.3, max: 0.8 },
              animation: { enable: true, speed: 0.9, minimumValue: 0.3 }
            },
            links: { enable: false }, // 发射的粒子不产生连接线
            move: {
              speed: 0.8,
              decay: 0.05,
              direction: "none",
              straight: false,
              random: true,
              outModes: { default: "destroy" } // 移出画布后销毁
            },
            life: {
              duration: { min: 5, max: 10 }, // 粒子的生命周期
              count: 1
            }
          }
        },
        detectRetina: true, // 为高分屏优化
        background: {
          opacity: 0 // 背景本身透明，以显示下面的CSS渐变
        }
      })
      console.log("tsParticles loaded successfully.")
    } catch (error) {
      console.error("Error loading tsParticles:", error)
    }
  } else {
    console.warn("tsParticles library not found.")
  }
}

/**
 * 动态加载 tsParticles 脚本
 */
const loadTsParticlesScript = (): Promise<void> => {
  return new Promise((resolve, reject) => {
    // 检查是否已经加载过
    if (document.querySelector('script[src*="tsparticles"]')) {
      resolve()
      return
    }

    const script = document.createElement('script')
    script.src = 'https://cdn.jsdelivr.net/npm/tsparticles@2.12.0/tsparticles.bundle.min.js'
    script.onload = () => resolve()
    script.onerror = () => reject(new Error('Failed to load tsParticles script'))
    document.head.appendChild(script)
  })
}

/**
 * 销毁粒子效果
 */
const destroyParticles = () => {
  if (particlesInstance) {
    particlesInstance.destroy()
    particlesInstance = null
  }
}

onMounted(() => {
  initParticles()
})

onUnmounted(() => {
  destroyParticles()
})
</script>

<style scoped>
/* --- 定义颜色和变量 --- */
:root {
  --glow-color: rgba(173, 216, 230, 0.7);
  /* 辉光颜色 (淡蓝色) */
  --sky-dark: #0b0f19;
  /* 非常深的蓝黑色 */
  --star-bright: #FFFFFF;
  /* 亮星星颜色 */
  --star-soft: #ADD8E6;
  /* 软星星颜色 (淡蓝色) */
  --star-pale: #F0F8FF;
  /* 灰白星星颜色 (爱丽丝蓝) */
}

.starry-background {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  z-index: -10;
  background-color: var(--sky-dark);
  /* 设置基础的深色背景 */
  overflow: hidden;
  /* 防止出现滚动条 */
}

/* --- 动态渐变背景 --- */
.starry-background::before {
  content: '';
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  /* 创建一个包含多种颜色的线性渐变 */
  background: linear-gradient(-45deg,

      /* 蓝色 */
      #7c3aed,
      /* 紫色 */
      /* #16a34a, */
      /* 绿色 */
      #0891b2,
      /* 青色 */
      #4f46e5,
      /* 靛蓝色 */
      /* #7c3aed */
      /* 紫红色 */
      #7c3aed
      /* 重复第一个颜色以实现平滑循环 */
    );
  background-size: 400% 400%;
  /* 扩大背景尺寸以获得更平滑的过渡 */
  animation: gradient-flow 20s linear infinite;
  /* 应用流动的动画 */
  z-index: -2;
  /* 将其置于粒子层之下 */
  opacity: 0.7;
  /* 调整透明度 */
}

/* --- 渐变背景的动画关键帧 --- */
@keyframes gradient-flow {
  0% {
    background-position: 0% 50%;
  }

  100% {
    background-position: 100% 50%;
  }
}

/* --- 粒子效果容器 --- */
#tsparticles {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  z-index: -1;
  /* 将其置于内容之下，渐变层之上 */
}
</style>