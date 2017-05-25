package com.athou.slidingcard;

import android.support.design.widget.CoordinatorLayout;
import android.support.v4.view.ViewCompat;
import android.view.View;

/**
 * Created by Administrator on 2017/4/24.
 */

public class SlidingCardBehavior extends CoordinatorLayout.Behavior<SlidingCardLayout> {

    int mInitOffset;

    @Override
    public boolean onMeasureChild(CoordinatorLayout parent, SlidingCardLayout child, int parentWidthMeasureSpec, int widthUsed, int parentHeightMeasureSpec, int heightUsed) {
        //测量child
        //卡片的高度=父容器的高度-上边和下边几个child的头部高度和
        int offset = getChildMeasureOffset(parent, child);
        int height = View.MeasureSpec.getSize(parentHeightMeasureSpec) - offset;
        child.measure(parentWidthMeasureSpec, View.MeasureSpec.makeMeasureSpec(height, View.MeasureSpec.AT_MOST));
        return true;
    }

    private int getChildMeasureOffset(CoordinatorLayout parent, SlidingCardLayout child) {
        int offset = 0;
        //上面和下面几个child的头部高度和
        for (int i = 0; i < parent.getChildCount(); i++) {
            View view = parent.getChildAt(i);
            if (view != child && view instanceof SlidingCardLayout) {
                offset += ((SlidingCardLayout) view).getHeaderHeight();
            }
        }
        return offset;
    }

    @Override
    public boolean onLayoutChild(CoordinatorLayout parent, SlidingCardLayout child, int layoutDirection) {
        parent.onLayoutChild(child, layoutDirection);
        //让child往下偏移多少 -- 上边几个child的头部的高度和
        SlidingCardLayout pre = getPreviousChild(parent, child);
        if (pre != null) {
            int offset = pre.getTop() + pre.getHeaderHeight();
            child.offsetTopAndBottom(offset);
        }
        mInitOffset = child.getTop();
        return true;
    }

    private SlidingCardLayout getPreviousChild(CoordinatorLayout parent, SlidingCardLayout child) {
        int index = parent.indexOfChild(child);
        for (int i = index - 1; i >= 0; i--) {
            View view = parent.getChildAt(i);
            if (view instanceof SlidingCardLayout) {
                return (SlidingCardLayout) view;
            }
        }
        return null;
    }

    private SlidingCardLayout getNextChild(CoordinatorLayout parent, SlidingCardLayout child) {
        int index = parent.indexOfChild(child);
        for (int i = index + 1; i <= parent.getChildCount(); i++) {
            View view = parent.getChildAt(i);
            if (view instanceof SlidingCardLayout) {
                return (SlidingCardLayout) view;
            }
        }
        return null;
    }

    @Override
    public boolean onStartNestedScroll(CoordinatorLayout parent, SlidingCardLayout child, View directTargetChild, View target, int nestedScrollAxes) {
        //控制滑动方向
        SlidingCardLayout pre = getPreviousChild(parent, child);
        if (pre == null) { //如果当前child为第一个view, 则禁止下滑
            return false;
        }
        boolean isVertical = (nestedScrollAxes & ViewCompat.SCROLL_AXIS_VERTICAL) != 0;
        return isVertical && (child == directTargetChild);
    }

    @Override
    public void onNestedPreScroll(CoordinatorLayout parent, SlidingCardLayout child, View target, int dx, int dy, int[] consumed) {
        //监听滑动情况 控制自己的滑动以及其他卡片的联动效果
        //手指滑动多少---child要偏移的值，dy；往上滑+，往下滑-
        //dy要控制在一个有效的范围
        int minOffset = mInitOffset;
        int maxOffset = mInitOffset + child.getHeight() - child.getHeaderHeight();
        int initialOffset = child.getTop();

        //1,控制自己的滑动
        int offset = clamp(initialOffset - dy, minOffset, maxOffset) - initialOffset;
        child.offsetTopAndBottom(offset);
        consumed[1] = -offset;
        //2，其他卡片的联动效果
        shiftSlidings(consumed[1], parent, child);
    }

    private void shiftSlidings(int shift, CoordinatorLayout parent, SlidingCardLayout child) {
        if (shift == 0) {
            return;
        }
        if (shift > 0) { //往上推
            SlidingCardLayout current = child;
            SlidingCardLayout pre = getPreviousChild(parent, child);
            while (pre != null) {
                int offset = getHeaderOverlap(pre, current);
                if (offset > 0) { //往上推要为负的
                    pre.offsetTopAndBottom(-offset);
                }
                current = pre;
                pre = getPreviousChild(parent, current);
            }
        } else {//往下推
            SlidingCardLayout current = child;
            SlidingCardLayout next = getNextChild(parent, child);
            while (next != null) {
                int offset = getHeaderOverlap(current, next);
                if (offset > 0) {
                    next.offsetTopAndBottom(offset);
                }
                current = next;
                next = getNextChild(parent, current);
            }
        }
    }

    //获取2个相邻card的间距差
    private int getHeaderOverlap(SlidingCardLayout above, SlidingCardLayout below) {
        return above.getTop() + above.getHeaderHeight() - below.getTop();
    }

    private int clamp(int i, int minOffset, int maxOffset) {
        if (i > maxOffset) {
            return maxOffset;
        } else if (i < minOffset) {
            return minOffset;
        } else {
            return i;
        }
    }

    @Override
    public boolean onNestedPreFling(CoordinatorLayout coordinatorLayout, SlidingCardLayout child, View target, float velocityX, float velocityY) {
        return super.onNestedPreFling(coordinatorLayout, child, target, velocityX, velocityY);
    }
}
