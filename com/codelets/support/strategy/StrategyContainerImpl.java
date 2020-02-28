package com.codelets.support.strategy;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.springframework.util.Assert;

import com.codelets.support.util.GenericExtractor;

import lombok.extern.slf4j.Slf4j;

/**
 * @param <C>
 *            条件类型
 * @param <S>
 *            策略类型
 */
@Slf4j
public abstract class StrategyContainerImpl<C, S extends IStrategy<C>> implements IStrategyContainer<C, S> {

	// 条件类型及策略实现类
	private final Map<C, S> strategyMap = new HashMap<C, S>();

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.tintinloan.icms.biz.strategy.IStrategyContainer#getStrategy(C)
	 */
	@Override
	public S getStrategy(final C condition) {
		if (!strategyMap.containsKey(condition)) {
			initStrategyBean();
		}
		return strategyMap.get(condition);
	}

	/**
	 * @return the strategyMap
	 */
	protected Map<C, S> getStrategyMap() {
		return strategyMap;
	}

	/**
	 * 从spring context中直接提取对应的策略处理器，用以取代手工的registStrategy
	 * 
	 * 只能应用于从StrategyContainerImpl继承的容器类，无法直接应用于StrategyContainerImpl对象
	 */
	protected synchronized void initStrategyBean() {
		if (strategyMap.size() > 0) {
			log.trace(this.getClass().getName() + "has already been initialed");
			return;
		}
		Assert.isTrue(this.getClass() != StrategyContainerImpl.class, "该方法无法直接应用于StrategyContainerImpl对象");

		// getGenericSuperclass()获得带有泛型的父类，返回Type
		// Type是 Java 编程语言中所有类型的公共高级接口。它们包括原始类型、参数化类型、数组类型、类型变量和基本类型。
		@SuppressWarnings("unchecked")
		final Class<S> strategyClass = (Class<S>) GenericExtractor.getClass(this.getClass().getGenericSuperclass(), 1);
		Assert.isTrue(IStrategy.class.isAssignableFrom(strategyClass));

		@SuppressWarnings("unchecked")
		final Class<C> conditionClass = (Class<C>) GenericExtractor.getClass(this.getClass().getGenericSuperclass(), 0);
		final Collection<S> strategys = ManageSpringBeans.getBeans(strategyClass).values();
		log.info(conditionClass.getName() + "[C] has " + strategys.size() + " " + strategyClass.getName() + "[S]");

		for (final S s : strategys) {
			log.debug("[" + s.getCondition() + "]" + "[" + s + "]");
			Assert.isTrue(!strategyMap.containsKey(s.getCondition()), "该类型已被注册过[" + s.getCondition() + "][" + s + "]");
			strategyMap.put(s.getCondition(), s);
		}
	}
}