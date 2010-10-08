/*
Copyright 2010 C�lio Cidral Junior

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
*/

package org.tomighty.ui.state;

import static java.awt.BorderLayout.CENTER;
import static java.awt.BorderLayout.NORTH;
import static java.awt.BorderLayout.SOUTH;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.GridLayout;
import java.awt.LayoutManager;

import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;

import org.tomighty.bus.Bus;
import org.tomighty.ioc.Inject;
import org.tomighty.ioc.Injector;
import org.tomighty.ui.UiState;
import org.tomighty.ui.layout.DockLayout;
import org.tomighty.ui.layout.Docking;

public abstract class UiStateSupport implements UiState {

	@Inject private Injector injector;
	@Inject protected Bus bus;
	
	protected abstract String title();
	protected abstract Component createContent();
	protected abstract Action[] primaryActions();
	protected abstract Action[] secondaryActions();
	
	@Override
	public final Component render() throws Exception {
		JPanel component = createComponent();
		Action[] secondaryActions = secondaryActions();
		if(secondaryActions == null) {
			return component;
		}
		return addSecondaryActionsTo(component, secondaryActions);
	}
	
	private JPanel createComponent() {
		JPanel component = createPanel();
		String title = title();
		if(title != null) {
			component.add(Label.small(title), NORTH);
		}
		component.add(createContent(), CENTER);
		component.add(createButtons(), SOUTH);
		return component;
	}
	
	private Component createButtons() {
		Action[] actions = primaryActions();
		JPanel buttons = createPanel(new GridLayout(1, actions.length, 3, 0));
		for(Action action : actions) {
			injector.inject(action);
			JButton button = new JButton(action);
			button.setOpaque(false);
			buttons.add(button);
		}
		return buttons;
	}
	
	private Component addSecondaryActionsTo(JPanel component, Action[] actions) {
		JPopupMenu menu = createSecondaryActionsMenu(actions);
		PopupMenuButton button = new PopupMenuButton(menu);
		JPanel panel = createPanel(new DockLayout());
		panel.add(component, Docking.fill());
		panel.add(button, Docking.rightTop(1, 1));
		return panel;
	}

	private JPopupMenu createSecondaryActionsMenu(Action[] actions) {
		JPopupMenu menu = new JPopupMenu();
		for(Action action : actions) {
			injector.inject(action);
			JMenuItem item = new JMenuItem(action);
			menu.add(item);
		}
		return menu;
	}
	
	private JPanel createPanel() {
		return createPanel(new BorderLayout());
	}
	
	private static final JPanel createPanel(LayoutManager layout) {
		JPanel panel = new JPanel(layout);
		panel.setOpaque(false);
		return panel;
	}

}