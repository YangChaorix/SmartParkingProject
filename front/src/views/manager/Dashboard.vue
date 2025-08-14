<template>
  <div class="dashboard-container">
    <div class="card main-chart">
      <div class="chart-header">
        <el-button type="primary" @click="downloadLineData">
          <el-icon>
            <Download />
          </el-icon>
          导出数据
        </el-button>
      </div>
      <div id="line"></div>
    </div>
    <div class="chart-grid">
      <div class="card" id="bar"></div>
      <div class="card" id="pie"></div>
    </div>
  </div>
</template>

<script setup>
import { reactive, onMounted, onUnmounted } from "vue";
import request from "@/utils/request.js";
import { ElMessage } from "element-plus";
import * as echarts from "echarts";
import { Download } from '@element-plus/icons-vue';

const data = reactive({
  user: JSON.parse(localStorage.getItem('xm-user') || '{}'),
  charts: []
})

// Chart resize handler
const handleResize = () => {
  data.charts.forEach(chart => chart.resize());
}

onMounted(() => {
  window.addEventListener('resize', handleResize);
});

onUnmounted(() => {
  window.removeEventListener('resize', handleResize);
});

const loadLine = () => {
  request.get('/dashboard/line').then(res => {
    if (res.code === '200') {
      console.log('接收到的折线图数据:', res.data);
      let chartDom = document.getElementById('line')
      if (!chartDom) {
        console.error('找不到line图表DOM元素');
        return;
      }

      // 如果已经有实例，先销毁
      const existingChart = data.charts.find(chart => chart.getDom() === chartDom);
      if (existingChart) {
        existingChart.dispose();
        data.charts = data.charts.filter(chart => chart !== existingChart);
      }

      let myChart = echarts.init(chartDom)
      data.charts[0] = myChart; // 确保折线图总是在第一个位置

      lineOptions.xAxis.data = res.data.xList
      lineOptions.series[0].data = res.data.yList
      myChart.setOption(lineOptions)

      console.log('折线图初始化完成，当前charts数组:', data.charts);
    } else {
      ElMessage.error(res.msg)
    }
  }).catch(error => {
    console.error('加载折线图数据失败:', error);
    ElMessage.error('加载数据失败，请刷新页面重试');
  })
}

const loadPie = () => {
  request.get('/dashboard/pie').then(res => {
    if (res.code === '200') {
      let chartDom = document.getElementById('pie')
      let myChart = echarts.init(chartDom)
      data.charts.push(myChart)
      pieOptions.series[0].data = res.data
      myChart.setOption(pieOptions)
    } else {
      ElMessage.error(res.msg)
    }
  })
}

const loadBar = () => {
  request.get('/dashboard/bar').then(res => {
    if (res.code === '200') {
      let chartDom = document.getElementById('bar')
      let myChart = echarts.init(chartDom)
      data.charts.push(myChart)
      barOptions.xAxis.data = res.data.xList
      barOptions.series[0].data = res.data.yList
      myChart.setOption(barOptions)
    } else {
      ElMessage.error(res.msg)
    }
  })
}

loadLine()
loadPie()
loadBar()

// 平滑折线图
let lineOptions = {
  title: {
    text: '车辆入场趋势',
    subtext: '最近一周数据',
    left: 'center',
    top: 20,
    textStyle: {
      fontSize: 18,
      fontWeight: 500,
      color: '#333'
    },
    subtextStyle: {
      fontSize: 13,
      color: '#666',
      fontWeight: 400
    }
  },
  grid: {
    left: '5%',
    right: '5%',
    bottom: '8%',
    top: '20%',
    containLabel: true
  },
  tooltip: {
    trigger: 'axis',
    backgroundColor: 'rgba(255, 255, 255, 0.95)',
    borderColor: 'rgba(149, 157, 165, 0.2)',
    borderWidth: 1,
    padding: [12, 16],
    textStyle: {
      color: '#333',
      fontSize: 13
    },
    axisPointer: {
      type: 'line',
      lineStyle: {
        color: 'rgba(149, 157, 165, 0.2)',
        width: 2
      }
    }
  },
  xAxis: {
    type: 'category',
    data: ['Mon', 'Tue', 'Wed', 'Thu', 'Fri', 'Sat', 'Sun'],
    axisLine: {
      lineStyle: {
        color: '#eaecef'
      }
    },
    axisTick: {
      show: false
    },
    axisLabel: {
      fontSize: 13,
      color: '#666',
      padding: [8, 0]
    }
  },
  yAxis: {
    type: 'value',
    splitLine: {
      lineStyle: {
        color: '#f0f2f5',
        width: 1
      }
    },
    axisLine: {
      show: false
    },
    axisTick: {
      show: false
    },
    axisLabel: {
      fontSize: 13,
      color: '#666',
      padding: [0, 8]
    }
  },
  series: [
    {
      name: '入场车辆',
      data: [120, 932, 2201, 1034, 1290, 1330, 3320],
      type: 'line',
      smooth: true,
      symbol: 'circle',
      symbolSize: 10,
      itemStyle: {
        color: '#007AFF',
        borderWidth: 2,
        borderColor: '#fff'
      },
      lineStyle: {
        width: 4,
        color: '#007AFF'
      },
      areaStyle: {
        color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
          { offset: 0, color: 'rgba(0, 122, 255, 0.2)' },
          { offset: 1, color: 'rgba(0, 122, 255, 0.02)' }
        ])
      }
    }
  ]
};

// 饼图数据结构
let pieOptions = {
  title: {
    text: '车位状态分布',
    left: 'center',
    top: 20,
    textStyle: {
      fontSize: 18,
      fontWeight: 500,
      color: '#333'
    }
  },
  tooltip: {
    trigger: 'item',
    backgroundColor: 'rgba(255, 255, 255, 0.95)',
    borderColor: 'rgba(149, 157, 165, 0.2)',
    borderWidth: 1,
    padding: [12, 16],
    textStyle: {
      color: '#333',
      fontSize: 13
    }
  },
  legend: {
    orient: 'horizontal',
    bottom: 20,
    icon: 'circle',
    itemWidth: 10,
    itemHeight: 10,
    itemGap: 20,
    textStyle: {
      fontSize: 13,
      color: '#666'
    }
  },
  series: [
    {
      name: '车位状态',
      type: 'pie',
      radius: ['45%', '70%'],
      center: ['50%', '50%'],
      avoidLabelOverlap: false,
      itemStyle: {
        borderRadius: 8,
        borderColor: '#fff',
        borderWidth: 3
      },
      label: {
        show: false
      },
      emphasis: {
        label: {
          show: true,
          fontSize: 14,
          fontWeight: 500
        },
        itemStyle: {
          shadowBlur: 10,
          shadowOffsetX: 0,
          shadowColor: 'rgba(0, 0, 0, 0.1)'
        }
      },
      labelLine: {
        show: false
      },
      data: [
        { value: 1048, name: '已占用' },
        { value: 735, name: '空闲' },
        { value: 580, name: '预约' },
        { value: 484, name: '维护' }
      ],
      color: ['#007AFF', '#34C759', '#5856D6', '#FF9500']
    }
  ]
}

// 柱状图
let barOptions = {
  title: {
    text: '区域车位分布',
    left: 'center',
    top: 20,
    textStyle: {
      fontSize: 18,
      fontWeight: 500,
      color: '#333'
    }
  },
  grid: {
    left: '5%',
    right: '5%',
    bottom: '15%',
    top: '20%',
    containLabel: true
  },
  tooltip: {
    trigger: 'axis',
    backgroundColor: 'rgba(255, 255, 255, 0.95)',
    borderColor: 'rgba(149, 157, 165, 0.2)',
    borderWidth: 1,
    padding: [12, 16],
    textStyle: {
      color: '#333',
      fontSize: 13
    },
    axisPointer: {
      type: 'shadow',
      shadowStyle: {
        color: 'rgba(0, 122, 255, 0.05)'
      }
    }
  },
  xAxis: {
    type: 'category',
    data: ['A区', 'B区', 'C区', 'D区', 'E区', 'F区', 'G区'],
    axisLabel: {
      interval: 0,
      rotate: 0,
      fontSize: 13,
      color: '#666',
      padding: [8, 0]
    },
    axisLine: {
      lineStyle: {
        color: '#eaecef'
      }
    },
    axisTick: {
      show: false
    }
  },
  yAxis: {
    type: 'value',
    splitLine: {
      lineStyle: {
        color: '#f0f2f5',
        width: 1
      }
    },
    axisLine: {
      show: false
    },
    axisTick: {
      show: false
    },
    axisLabel: {
      fontSize: 13,
      color: '#666',
      padding: [0, 8]
    }
  },
  series: [
    {
      data: [120, 200, 150, 80, 70, 110, 130],
      type: 'bar',
      barWidth: '50%',
      itemStyle: {
        color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
          { offset: 0, color: '#007AFF' },
          { offset: 1, color: '#0066FF' }
        ]),
        borderRadius: [8, 8, 0, 0]
      },
      emphasis: {
        itemStyle: {
          color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
            { offset: 0, color: '#0066FF' },
            { offset: 1, color: '#0052CC' }
          ])
        }
      }
    }
  ]
};

// 下载折线图数据
const downloadLineData = () => {
  // console.log('开始导出数据...');
  // console.log('Charts数组:', data.charts);

  const chart = data.charts[0]; // 获取折线图实例
  if (!chart) {
    ElMessage.error('图表数据未加载，请稍后再试');
    console.error('图表实例未找到');
    return;
  }

  try {
    const option = chart.getOption();
    console.log('获取到的图表配置:', option);

    // 直接使用原始数据
    const dates = option.xAxis[0].data;
    const values = option.series[0].data;

    console.log('准备导出的数据:', { dates, values });

    // 如果完全没有数据，才提示错误
    if (!dates || !values || dates.length === 0 || values.length === 0) {
      ElMessage.error('暂无可导出的数据');
      return;
    }

    // 导出所有数据
    const csvContent = 'data:text/csv;charset=utf-8,'
      + '日期,入场车辆数\n'
      + dates.map((date, i) => `${date},${values[i]}`).join('\n');

    const link = document.createElement('a');
    link.href = encodeURI(csvContent);
    link.download = '车辆入场趋势数据.csv';
    document.body.appendChild(link);
    link.click();
    document.body.removeChild(link);

    ElMessage.success('数据导出成功');
  } catch (error) {
    console.error('导出数据时发生错误:', error);
    ElMessage.error('导出数据失败，请稍后重试');
  }
};
</script>

<style scoped>
.dashboard-container {
  padding: 24px;
  background-color: #f8f9fd;
  min-height: 100vh;
  font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, sans-serif;
}

.card {
  background: #fff;
  border-radius: 16px;
  box-shadow: 0 8px 24px rgba(149, 157, 165, 0.1);
  padding: 24px;
  transition: all 0.3s ease;
}

.card:hover {
  transform: translateY(-2px);
  box-shadow: 0 12px 32px rgba(149, 157, 165, 0.15);
}

.main-chart {
  height: 420px;
  margin-bottom: 24px;
  position: relative;
}

#line {
  width: 100%;
  height: 100%;
}

.chart-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 24px;
}

.chart-grid .card {
  height: 380px;
}

.chart-header {
  position: absolute;
  top: 16px;
  right: 16px;
  z-index: 10;
}

.el-button {
  padding: 8px 16px;
  display: inline-flex;
  align-items: center;
  gap: 4px;
  background-color: #007AFF;
  border-color: #007AFF;
}

.el-button:hover {
  background-color: #0066FF;
  border-color: #0066FF;
}

.el-button .el-icon {
  font-size: 16px;
}

@media (max-width: 768px) {
  .chart-grid {
    grid-template-columns: 1fr;
  }

  .dashboard-container {
    padding: 16px;
  }
}
</style>
