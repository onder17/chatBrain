export interface ChatRequestCallbacks<T> {
  onLoadingChange: (loading: boolean) => void;
  onSuccess: (value: T) => void;
  onFailure: (error: unknown) => void;
}

/** Shared request lifecycle so loading, success, and failure transitions are testable. */
export async function runChatRequest<T>(
  send: () => Promise<T>,
  callbacks: ChatRequestCallbacks<T>,
): Promise<void> {
  callbacks.onLoadingChange(true);
  try {
    callbacks.onSuccess(await send());
  } catch (error) {
    callbacks.onFailure(error);
  } finally {
    callbacks.onLoadingChange(false);
  }
}
