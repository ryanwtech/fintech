import { useState } from 'react';
import { useRules, useCreateRule, useUpdateRule, useDeleteRule, useToggleRule, useReorderRules } from '../hooks/useRules';
import { RuleForm } from '../components/RuleForm';
import { DraggableRuleRow } from '../components/DraggableRuleRow';
import { TableSkeletonLoader } from '../components/SkeletonLoader';
import { Plus, GripVertical } from 'lucide-react';
import type { Rule, CreateRuleRequest, UpdateRuleRequest } from '../types';

export const RulesPage = () => {
  const [showForm, setShowForm] = useState(false);
  const [editingRule, setEditingRule] = useState<Rule | null>(null);
  const [draggedRule, setDraggedRule] = useState<Rule | null>(null);

  const { data: rules, isLoading } = useRules();
  const createRule = useCreateRule();
  const updateRule = useUpdateRule();
  const deleteRule = useDeleteRule();
  const toggleRule = useToggleRule();
  const reorderRules = useReorderRules();

  const handleCreate = () => {
    setEditingRule(null);
    setShowForm(true);
  };

  const handleEdit = (rule: Rule) => {
    setEditingRule(rule);
    setShowForm(true);
  };

  const handleFormSubmit = async (data: CreateRuleRequest | UpdateRuleRequest) => {
    try {
      if (editingRule) {
        await updateRule.mutateAsync({ id: editingRule.id, data: data as UpdateRuleRequest });
      } else {
        await createRule.mutateAsync(data as CreateRuleRequest);
      }
      setShowForm(false);
      setEditingRule(null);
    } catch (error) {
      console.error('Failed to save rule:', error);
    }
  };

  const handleFormCancel = () => {
    setShowForm(false);
    setEditingRule(null);
  };

  const handleDelete = async (id: string) => {
    try {
      await deleteRule.mutateAsync(id);
    } catch (error) {
      console.error('Failed to delete rule:', error);
    }
  };

  const handleToggle = async (id: string, enabled: boolean) => {
    try {
      await toggleRule.mutateAsync({ id, enabled });
    } catch (error) {
      console.error('Failed to toggle rule:', error);
    }
  };

  const handleDragStart = (rule: Rule) => {
    setDraggedRule(rule);
  };

  const handleDragEnd = () => {
    setDraggedRule(null);
  };

  const handleDrop = async (targetRule: Rule) => {
    if (!draggedRule || draggedRule.id === targetRule.id) {
      return;
    }

    const currentRules = rules || [];
    const draggedIndex = currentRules.findIndex(r => r.id === draggedRule.id);
    const targetIndex = currentRules.findIndex(r => r.id === targetRule.id);

    if (draggedIndex === -1 || targetIndex === -1) {
      return;
    }

    // Create new order
    const newRules = [...currentRules];
    const [removed] = newRules.splice(draggedIndex, 1);
    newRules.splice(targetIndex, 0, removed);

    // Update priorities
    const reorderedRules = newRules.map((rule, index) => ({
      ...rule,
      priority: index
    }));

    try {
      await reorderRules.mutateAsync({
        ruleIds: reorderedRules.map(r => r.id)
      });
    } catch (error) {
      console.error('Failed to reorder rules:', error);
    }
  };

  const sortedRules = rules ? [...rules].sort((a, b) => (a.priority || 0) - (b.priority || 0)) : [];

  return (
    <div className="space-y-6">
      <div className="flex justify-between items-center">
        <div>
          <h1 className="text-2xl font-bold text-gray-900">Rules</h1>
          <p className="mt-1 text-sm text-gray-500">
            Create rules to automatically categorize transactions based on patterns.
          </p>
        </div>
        <button
          onClick={handleCreate}
          className="inline-flex items-center px-4 py-2 border border-transparent text-sm font-medium rounded-md shadow-sm text-white bg-indigo-600 hover:bg-indigo-700"
        >
          <Plus className="h-4 w-4 mr-2" />
          Create Rule
        </button>
      </div>

      {/* Rules Table */}
      <div className="bg-white shadow rounded-lg">
        <div className="px-4 py-5 sm:p-6">
          <div className="flex items-center justify-between mb-4">
            <h3 className="text-lg leading-6 font-medium text-gray-900">
              Transaction Rules
              {rules && (
                <span className="ml-2 text-sm font-normal text-gray-500">
                  ({rules.length} total)
                </span>
              )}
            </h3>
          </div>

          {isLoading ? (
            <TableSkeletonLoader rows={5} columns={6} />
          ) : !rules || rules.length === 0 ? (
            <div className="text-center py-12">
              <h3 className="text-lg font-medium text-gray-900">No rules found</h3>
              <p className="mt-1 text-sm text-gray-500">
                Get started by creating your first rule to automatically categorize transactions.
              </p>
            </div>
          ) : (
            <div className="overflow-hidden shadow ring-1 ring-black ring-opacity-5 md:rounded-lg">
              <table className="min-w-full divide-y divide-gray-300">
                <thead className="bg-gray-50">
                  <tr>
                    <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                      <GripVertical className="h-4 w-4" />
                    </th>
                    <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                      Priority
                    </th>
                    <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                      Name
                    </th>
                    <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                      Pattern
                    </th>
                    <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                      Target Category
                    </th>
                    <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                      Status
                    </th>
                    <th className="px-6 py-3 text-right text-xs font-medium text-gray-500 uppercase tracking-wider">
                      Actions
                    </th>
                  </tr>
                </thead>
                <tbody className="bg-white divide-y divide-gray-200">
                  {sortedRules.map((rule) => (
                    <DraggableRuleRow
                      key={rule.id}
                      rule={rule}
                      onEdit={handleEdit}
                      onDelete={handleDelete}
                      onToggle={handleToggle}
                      onTest={() => {}}
                      isDragging={draggedRule?.id === rule.id}
                      dragHandleProps={{
                        draggable: true,
                        onDragStart: () => handleDragStart(rule),
                        onDragEnd: handleDragEnd,
                        onDragOver: (e: React.DragEvent) => e.preventDefault(),
                        onDrop: () => handleDrop(rule),
                      }}
                    />
                  ))}
                </tbody>
              </table>
            </div>
          )}
        </div>
      </div>

      {/* Rule Form Modal */}
      {showForm && (
        <div className="fixed inset-0 z-50 overflow-y-auto">
          <div className="flex min-h-screen items-end justify-center px-4 pt-4 pb-20 text-center sm:block sm:p-0">
            <div className="fixed inset-0 bg-gray-500 bg-opacity-75 transition-opacity" onClick={handleFormCancel} />

            <span className="hidden sm:inline-block sm:align-middle sm:h-screen" aria-hidden="true">
              &#8203;
            </span>

            <div className="relative inline-block transform overflow-hidden rounded-lg bg-white text-left align-bottom shadow-xl transition-all sm:my-8 sm:w-full sm:max-w-2xl sm:align-middle">
              <div className="bg-white px-4 pt-5 pb-4 sm:p-6 sm:pb-4">
                <div className="flex items-center justify-between mb-4">
                  <h3 className="text-lg font-medium text-gray-900">
                    {editingRule ? 'Edit Rule' : 'Create New Rule'}
                  </h3>
                </div>

                <RuleForm
                  rule={editingRule}
                  onSubmit={handleFormSubmit}
                  onCancel={handleFormCancel}
                  isLoading={createRule.isPending || updateRule.isPending}
                />
              </div>
            </div>
          </div>
        </div>
      )}

      {/* Help Section */}
      <div className="bg-blue-50 border border-blue-200 rounded-lg p-6">
        <h3 className="text-lg font-medium text-blue-900 mb-2">How Rules Work</h3>
        <div className="text-sm text-blue-800 space-y-2">
          <p>
            Rules automatically categorize transactions based on patterns in their descriptions or merchant names.
          </p>
          <ul className="list-disc list-inside space-y-1 ml-4">
            <li>Rules are processed in priority order (lower numbers first)</li>
            <li>Use regular expressions to create flexible patterns</li>
            <li>Test your patterns before saving to ensure they work correctly</li>
            <li>Only enabled rules are applied to new transactions</li>
            <li>Drag and drop to reorder rules by priority</li>
          </ul>
        </div>
      </div>
    </div>
  );
};